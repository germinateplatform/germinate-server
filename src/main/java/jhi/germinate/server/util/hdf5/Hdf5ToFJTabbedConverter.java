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
import jhi.flapjack.io.Hdf5Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author The Flapjack authors (https://ics.hutton.ac.uk/flapjack)
 */
public class Hdf5ToFJTabbedConverter extends AbstractHdf5Converter
{
	private boolean transposed;

	public Hdf5ToFJTabbedConverter(Path hdf5File, Set<String> lines, Set<String> markers, Path outputFilePath, boolean transposed)
	{
		super(hdf5File, lines, markers, outputFilePath);

		this.transposed = transposed;
	}

	/**
	 * Updates the given HDF5 file by replacing all occurrences of germplasm in otherNames with the preferredName.
	 *
	 * @param hdf5File      The HDF5 file to update
	 * @param preferredName The preferred name to replace the others with
	 * @param otherNames    The other names to replace
	 */
	public static synchronized void updateGermplasmNames(File hdf5File, String preferredName, List<String> otherNames)
	{
		// Update the names
		List<String> oldNames = Hdf5Utils.getLines(hdf5File);
		for (int i = 0; i < oldNames.size(); i++)
		{
			if (otherNames.contains(oldNames.get(i)))
			{
				oldNames.set(i, preferredName);
			}
		}

		// Write them back to the file
		try (IHDF5Writer writer = HDF5Factory.open(hdf5File))
		{
			writer.string().writeArray(LINES, oldNames.toArray(new String[0]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);
		}
	}

	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();

		// Write our output file line by line
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8));
			 IHDF5Reader reader = HDF5Factory.openForReading(hdf5File.toFile()))
		{
			String[] stateTable = reader.readStringArray(STATE_TABLE);
			System.out.println("Read statetable: " + (System.currentTimeMillis() - s) + " (ms)");

			// Write header for drag and drop
			writer.println("# fjFile = GENOTYPE");

			// Output any extra header lines that have been provided such as db link urls
			if (headerLines != null && !headerLines.isEmpty())
				writer.print(headerLines);

			if (transposed)
			{
				// Write the header line of a Flapjack file
				List<Integer> lineIndices = new ArrayList<>();
				writer.print("Marker/Accession");
				for (String line : lines)
				{
					lineIndices.add(lineInds.get(line));
					writer.print("\t" + line);
				}
				writer.println();

				s = System.currentTimeMillis();

				for (String markerName : markers)
				{
					// Read in a marker row (all of its alleles from file)
					// Get from DATA, lineInds.size(), 1 column, start from row 0 and column markerInds.get(markerName).
					// The resulting 2d array only contains one 1d array. Take that as the marker genotype data.
					byte[][] g = reader.int8().readMatrixBlock(DATA, hdf5Lines.size(), 1, 0, markerInds.get(markerName));
					byte[] genotypes = new byte[g.length];
					for (int i = 0; i < g.length; i++)
						genotypes[i] = g[i][0];
					writeGenotypeFlatFileString(writer, markerName, genotypes, lineIndices, stateTable);
				}
			}
			else
			{
				// Write the header line of a Flapjack file
				List<Integer> markerIndices = new ArrayList<>();
				writer.print("Accession/Marker");
				for (String marker : markers)
				{
					markerIndices.add(markerInds.get(marker));
					writer.print("\t" + marker);
				}
				writer.println();

				s = System.currentTimeMillis();

				for (String lineName : lines)
				{
					// Read in a line (all of its alleles from file)
					// Get from DATA, 1 row, markerInds.size() columns, start from row lineInds.get(lineName) and column 0.
					// The resulting 2d array only contains one 1d array. Take that as the lines genotype data.
					byte[] genotypes = reader.int8().readMatrixBlock(DATA, 1, markerInds.size(), lineInds.get(lineName), 0)[0];
					writeGenotypeFlatFileString(writer, lineName, genotypes, markerIndices, stateTable);
				}
			}
			System.out.println("Output lines to genotype file: " + (System.currentTimeMillis() - s) + " (ms)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println();
		System.out.println("HDF5 file converted to Flapjack genotype format");
	}

	private void writeGenotypeFlatFileString(PrintWriter bw, String lineName, byte[] genotypes, List<Integer> markerIndices, String[] stateTable)
	{
		// Collect the alleles which match the line and markers we're looking for
		bw.write(lineName);
		for (Integer index : markerIndices) {
			bw.write("\t" + stateTable[genotypes[index]]);
		}
		bw.println();
	}
}