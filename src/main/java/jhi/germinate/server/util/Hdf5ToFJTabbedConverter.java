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

package jhi.germinate.server.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import ch.systemsx.cisd.hdf5.*;

/**
 * @author The Flapjack authors (https://ics.hutton.ac.uk/flapjack)
 */
public class Hdf5ToFJTabbedConverter extends AbstractHdf5Converter
{
	private boolean transposed;

	public Hdf5ToFJTabbedConverter(File hdf5File, Set<String> lines, Set<String> markers, String outputFilePath, boolean transposed)
	{
		super(hdf5File, lines, markers, outputFilePath);

		this.transposed = transposed;
	}

	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();

		// Write our output file line by line
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)));
			 IHDF5Reader reader = HDF5Factory.openForReading(hdf5File))
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
				List<Integer> lineIndices = lines.parallelStream().map(line -> lineInds.get(line)).collect(Collectors.toList());
				// Write the header line of a Flapjack file
				writer.println(lines.parallelStream().collect(Collectors.joining("\t", "Marker/Accession\t", "")));

				s = System.currentTimeMillis();

				markers.forEach(markerName ->
				{
					// Read in a marker row (all of its alleles from file)
					// Get from DATA, lineInds.size(), 1 column, start from row 0 and column markerInds.get(markerName).
					// The resulting 2d array only contains one 1d array. Take that as the marker genotype data.
					byte[][] g = reader.int8().readMatrixBlock(DATA, lineInds.size(), 1, 0, markerInds.get(markerName));
					byte[] genotypes = new byte[g.length];
					for (int i = 0; i < g.length; i++)
						genotypes[i] = g[i][0];
					String outputGenotypes = createGenotypeFlatFileString(markerName, genotypes, lineIndices, stateTable);
					writer.println(outputGenotypes);
				});
			}
			else
			{
				List<Integer> markerIndices = markers.parallelStream().map(marker -> markerInds.get(marker)).collect(Collectors.toList());
				// Write the header line of a Flapjack file
				writer.println(markers.parallelStream().collect(Collectors.joining("\t", "Accession/Marker\t", "")));

				s = System.currentTimeMillis();

				lines.forEach(lineName ->
				{
					// Read in a line (all of its alleles from file)
					// Get from DATA, 1 row, markerInds.size() columns, start from row lineInds.get(lineName) and column 0.
					// The resulting 2d array only contains one 1d array. Take that as the lines genotype data.
					byte[] genotypes = reader.int8().readMatrixBlock(DATA, 1, markerInds.size(), lineInds.get(lineName), 0)[0];
					String outputGenotypes = createGenotypeFlatFileString(lineName, genotypes, markerIndices, stateTable);
					writer.println(outputGenotypes);
				});
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

	private String createGenotypeFlatFileString(String lineName, byte[] genotypes, List<Integer> markerIndices, String[] stateTable)
	{
		// Collect the alleles which match the line and markers we're looking for
		return markerIndices.parallelStream()
							.map(index -> genotypes[index])
							.map(allele -> stateTable[allele])
							.collect(Collectors.joining("\t", lineName + "\t", ""));
	}
}