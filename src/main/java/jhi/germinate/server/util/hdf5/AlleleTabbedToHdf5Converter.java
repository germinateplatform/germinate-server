/*
 *  Copyright 2018 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.server.util.hdf5;

import ch.systemsx.cisd.hdf5.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author The Flapjack authors (https://ics.hutton.ac.uk/flapjack)
 */
public class AlleleTabbedToHdf5Converter
{
	private static final int CHUNK_SIZE       = 100;
	private static final int CHUNK_SIZE_SMALL = 10;

	private static final String LINES       = "Lines";
	private static final String MARKERS     = "Markers";
	private static final String DATA        = "DataMatrix";

	private File alleleFile;
	private File hdf5File;
	private int     skipLines = 0;
	private boolean transpose = false;

	public AlleleTabbedToHdf5Converter(File alleleFile, File hdf5File)
	{
		this.alleleFile = alleleFile;
		this.hdf5File = hdf5File;
	}

	public void setSkipLines(int skipLines)
	{
		this.skipLines = skipLines;
	}

	public void setTranspose(boolean transpose)
	{
		this.transpose = transpose;
	}

	private void checkFileExists(File file)
	{
		if (!file.exists())
			System.err.println("Genotype file doesn't exist. Please specify a valid genotype file.");
	}

	public void convertToHdf5()
	{
		checkFileExists(alleleFile);

		// Delete old files with this name, because otherwise the new data will get appended to the old data
		if (hdf5File.exists() && hdf5File.isFile())
			hdf5File.delete();

		long s = System.currentTimeMillis();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(alleleFile), StandardCharsets.UTF_8));
		     // The second reader is just to get the number of rows
		     LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(new FileInputStream(alleleFile), StandardCharsets.UTF_8));
		     IHDF5Writer writer = HDF5Factory.open(hdf5File))
		{
			int counter = 0;

			// Count the number of header rows and skip them
			int offset = 0;
			String line = reader.readLine();
			while (line.length() == 0 || line.startsWith("#"))
			{
				offset++;
				line = reader.readLine();
			}

			for (int i = 0; i < skipLines; i++)
			{
				offset++;
				line = reader.readLine();
			}

			// Skip to the end
			lineNumberReader.skip(Long.MAX_VALUE);

			// Get the number of actual data rows
			int nrOfRows = lineNumberReader.getLineNumber() - 1 - offset;

			// We need to generate a mapping between the marker indices in the
			// genotype file and those in the map file
			String[] tokens = line.split("\t", -1);
			String[] markers = Arrays.copyOfRange(tokens, 1, tokens.length);

			// Remember the line names
			List<String> lines = new ArrayList<>();

			// Here we determine the size of the chunks within the matrix.
			// HDF5 has a hard limit of 4GB per chunk, so we need to set the chunk sizes appropriately.
			// Since we are using a float32 and not an int8, we need to divide by 4.
			long fourGig = 4L * 1024L * 1024L * 1024L / 4L;

			int localChunkSize = CHUNK_SIZE;

			if (markers.length > 2_000_000)
			{
				localChunkSize = CHUNK_SIZE_SMALL;
			}

			if (transpose)
			{
				// The number of rows is at least one and then depends on the number of times we can fit all the lines into 4GB
				int verticalChunk = (int) Math.min(markers.length, Math.max(1, Math.floor(fourGig / (nrOfRows * 1d))));
				// The number of columns is at most the number of lines and if the row is more than 4GB, then it's  the maximal number of columns that fit in 4GB
				int horizontalChunk = (int) Math.min(nrOfRows, fourGig);

				// Create the matrix based on the number of rows and the number of markers
				writer.float32().createMatrix(DATA, markers.length, nrOfRows, verticalChunk, horizontalChunk);

				List<float[]> cache = new ArrayList<>();
				while ((line = reader.readLine()) != null)
				{
					String[] columns = line.split("\t", -1);

					// Remember the line name
					lines.add(columns[0]);

					// The actual SNP calls are all but the first element of the split line
					String[] snpCalls = Arrays.copyOfRange(columns, 1, columns.length);

					Float[] floats = Stream.of(snpCalls).map(str -> {
						try {
							return Float.parseFloat(str);
						} catch (Exception e) {
							return 0f;
						}
					}).toArray(Float[]::new);
					float[] outFloats = convertFloatsToPrimitive(floats);

					if (outFloats.length != markers.length)
						continue;

					cache.add(outFloats);

					if (cache.size() >= localChunkSize)
					{
						writeCacheTransposed(writer, cache, markers.length, counter);
						counter += cache.size();
						cache.clear();
						System.out.println("Processed: " + counter);
					}
				}

				if (cache.size() > 0)
				{
					writeCacheTransposed(writer, cache, markers.length, counter);
				}
			}
			else
			{
				// The number of rows is at least one and then depends on the number of times we can fit all the markers into 4GB
				int verticalChunk = (int) Math.min(nrOfRows, Math.max(1, Math.floor(fourGig / (markers.length * 1d))));
				// The number of columns is at most the number of markers and if the row is more than 4GB, then it's  the maximal number of columns that fit in 4GB
				int horizontalChunk = (int) Math.min(markers.length, fourGig);

				// Create the matrix based on the number of rows and the number of markers
				writer.float32().createMatrix(DATA, nrOfRows, markers.length, verticalChunk, horizontalChunk);

				List<float[]> cache = new ArrayList<>();
				while ((line = reader.readLine()) != null)
				{
					String[] columns = line.split("\t", -1);

					// Remember the line name
					lines.add(columns[0]);

					// The actual SNP calls are all but the first element of the split line
					String[] snpCalls = Arrays.copyOfRange(columns, 1, columns.length);

					Float[] floats = Stream.of(snpCalls).map(str -> {
						try {
							return Float.parseFloat(str);
						} catch (Exception e) {
							return 0f;
						}
					}).toArray(Float[]::new);
					float[] outFloats = convertFloatsToPrimitive(floats);

					if (outFloats.length != markers.length)
						continue;

					cache.add(outFloats);

					if (cache.size() >= localChunkSize)
					{
						writeCache(writer, cache, markers.length, counter);
						counter += cache.size();
						cache.clear();
						System.out.println("Processed: " + counter);
					}
				}

				if (cache.size() > 0)
				{
					writeCache(writer, cache, markers.length, counter);
				}
			}

			// Write the marker and line names as arrays
			writer.string().writeArray(MARKERS, markers, HDF5GenericStorageFeatures.GENERIC_DEFLATE);
			writer.string().writeArray(LINES, lines.toArray(new String[0]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Took: " + ((System.currentTimeMillis() - s) / 1000f));
	}

	private void writeCache(IHDF5Writer writer, List<float[]> cache, int width, int startPosition)
	{
		float[][] outMatrixBytes = new float[cache.size()][width];
		for (int j = 0; j < cache.size(); j++)
		{
			outMatrixBytes[j] = cache.get(j);
		}
		writer.float32().writeMatrixBlockWithOffset(DATA, outMatrixBytes, startPosition, 0);
	}

	private void writeCacheTransposed(IHDF5Writer writer, List<float[]> cache, int width, int startPosition)
	{
		float[][] outMatrixBytes = new float[width][cache.size()];
		for (int j = 0; j < cache.size(); j++)
		{
			float[] outBytes = cache.get(j);

			for (int i = 0; i < outBytes.length; i++)
				outMatrixBytes[i][j] = outBytes[i];
		}
		writer.float32().writeMatrixBlockWithOffset(DATA, outMatrixBytes, 0, startPosition);
	}

	private float[] convertFloatsToPrimitive(Float[] floats)
	{
		float[] outFloats = new float[floats.length];
		int i = 0;
		for (Float f : floats)
			outFloats[i++] = f;

		return outFloats;
	}
}