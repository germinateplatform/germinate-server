package jhi.germinate.server.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import ch.systemsx.cisd.hdf5.*;

/**
 * @author Sebastian Raubach
 */
public class Hdf5ToHapmapConverter extends AbstractHdf5Converter
{
	private Map<String, MarkerPosition> map;

	public Hdf5ToHapmapConverter(File hdf5File, Set<String> lines, Set<String> markers, Map<String, MarkerPosition> map, String outputFilePath)
	{
		super(hdf5File, lines, markers, outputFilePath);

		this.map = map;
	}

	@Override
	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		List<Integer> lineIndices = lines.stream().map(line -> lineInds.get(line)).collect(Collectors.toList());
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();

		// Write our output file line by line
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)));
			 IHDF5Reader reader = HDF5Factory.openForReading(hdf5File))
		{
			String[] stateTable = reader.readStringArray(STATE_TABLE);
			System.out.println("Read statetable: " + (System.currentTimeMillis() - s) + " (ms)");

			// Output any extra header lines that have been provided such as db link urls
			if (headerLines != null && !headerLines.isEmpty())
				writer.print(headerLines);


			// Write the header line of a Flapjack file
			writer.println(lines.stream().collect(Collectors.joining("\t", "rs#\talleles\tchrom\tpos\tstrand\tassembly#\tcenter\tprotLSID\tassayLSID\tpanelLSID\tQCcode\t", "")));

			s = System.currentTimeMillis();

			markers.forEach(markerName ->
			{
				MarkerPosition mp = map.get(markerName);

				if (mp == null)
					mp = new MarkerPosition("NA", "1");

				writer.print(markerName + "\tNA\t" + mp.chromosome + "\t" + mp.position + "\tNA\tNA\tNA\tNA\tNA\tNA\tNA");

				// Read in a marker row (all of its alleles from file)
				// Get from DATA, lineInds.size(), 1 column, start from row 0 and column markerInds.get(markerName).
				// The resulting 2d array only contains one 1d array. Take that as the marker genotype data.
				byte[][] g = reader.int8().readMatrixBlock(DATA, lineInds.size(), 1, 0, markerInds.get(markerName));
				byte[] genotypes = new byte[g.length];
				for (int i = 0; i < g.length; i++)
					genotypes[i] = g[i][0];
				String outputGenotypes = lineIndices.stream()
													.map(index -> genotypes[index])
													.map(allele -> {
														String state = stateTable[allele];

														// Replace slashes
														if (state.contains("/"))
															state = state.replace("/", "");

														if (StringUtils.isEmpty(state))
															state = "N";

														// Duplicate individual nucleotides
														if (state.length() == 1)
															state = state + state;

														return state;
													})
													.collect(Collectors.joining("\t", "\t", ""));
				writer.println(outputGenotypes);
			});
			System.out.println("Output lines to genotype file: " + (System.currentTimeMillis() - s) + " (ms)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println();
		System.out.println("HDF5 file converted to HapMap genotype format");
	}

	public static class MarkerPosition
	{
		public String chromosome;
		public String position;

		public MarkerPosition(String chromosome, String position)
		{
			this.chromosome = chromosome;
			this.position = position;
		}
	}
}
