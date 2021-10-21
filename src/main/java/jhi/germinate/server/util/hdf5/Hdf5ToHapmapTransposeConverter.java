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
import jhi.germinate.server.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author The Flapjack authors (https://ics.hutton.ac.uk/flapjack)
 */
public class Hdf5ToHapmapTransposeConverter extends AbstractHdf5Converter
{
	private final Map<String, Hdf5ToHapmapConverter.MarkerPosition> map;

	public Hdf5ToHapmapTransposeConverter(Path hdf5File, Set<String> lines, Set<String> markers, Map<String, Hdf5ToHapmapConverter.MarkerPosition> map, Path outputFilePath)
	{
		super(hdf5File, lines, markers, outputFilePath);

		this.map = map;
	}

	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();

		try
		{
			Path temp = Files.createTempFile("hapmap-transpose", "txt");

			// Write our output file line by line
			try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(temp, StandardCharsets.UTF_8));
				 IHDF5Reader reader = HDF5Factory.openForReading(hdf5File.toFile()))
			{
				String[] stateTable = reader.readStringArray(STATE_TABLE);
				System.out.println("Read statetable: " + (System.currentTimeMillis() - s) + " (ms)");

				// Output any extra header lines that have been provided such as db link urls
				if (headerLines != null && !headerLines.isEmpty())
					writer.print(headerLines);

				List<Integer> markerIndices = markers.stream().map(marker -> markerInds.get(marker)).collect(Collectors.toList());
				// Write the header line of a Flapjack file
				String naFinal = markers.stream().map(m -> "NA").collect(Collectors.joining("\t", "\t", ""));
				writer.println(markers.stream().collect(Collectors.joining("\t", "rs#\t", "")));
				writer.println("alleles" + naFinal);
				writer.println(markers.stream().collect(Collectors.joining("\t", "chrom\t", "")));
				writer.println(markers.stream().collect(Collectors.joining("\t", "pos\t", "")));
				writer.println("strand" + naFinal);
				writer.println("assembly#" + naFinal);
				writer.println("center" + naFinal);
				writer.println("protLSID" + naFinal);
				writer.println("assayLSID" + naFinal);
				writer.println("panelLSID" + naFinal);
				writer.println("QCcode" + naFinal);

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
				System.out.println("Output lines to genotype file: " + (System.currentTimeMillis() - s) + " (ms)");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			new Transpose(temp, outputFilePath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println();
		System.out.println("HDF5 file converted to Flapjack genotype format");
	}

	private String createGenotypeFlatFileString(String lineName, byte[] genotypes, List<Integer> markerIndices, String[] stateTable)
	{
		// Collect the alleles which match the line and markers we're looking for
		return markerIndices.stream()
							.map(index -> genotypes[index])
							.map(allele -> {
								String state = stateTable[allele];

								if (StringUtils.isEmpty(state))
									state = "N";

								// Replace slashes
								if (state.contains("/"))
									state = state.replace("/", "");

								// Duplicate individual nucleotides
								if (state.length() == 1)
									state = state + state;

								return state;
							})
							.collect(Collectors.joining("\t", lineName + "\t", ""));
	}
}