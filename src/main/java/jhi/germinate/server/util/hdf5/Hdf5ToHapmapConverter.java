package jhi.germinate.server.util.hdf5;

import ch.systemsx.cisd.hdf5.*;
import jhi.germinate.server.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class Hdf5ToHapmapConverter extends AbstractHdf5Converter
{
	private Map<String, MarkerPosition> map;

	public Hdf5ToHapmapConverter(Path hdf5File, Set<String> lines, Set<String> markers, Map<String, MarkerPosition> map, Path outputFilePath)
	{
		super(hdf5File, lines, markers, outputFilePath);

		this.map = map;
	}

	@Override
	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		List<Integer> lineIndices = new ArrayList<>();
		for (String line : lines)
			lineIndices.add(lineInds.get(line));
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();

		// Write our output file line by line
		try (BufferedWriter bw = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
			 IHDF5Reader reader = HDF5Factory.openForReading(hdf5File.toFile()))
		{
			String[] stateTable = reader.readStringArray(STATE_TABLE);
			System.out.println("Read statetable: " + (System.currentTimeMillis() - s) + " (ms)");

			// Output any extra header lines that have been provided
			if (headerLines != null && !headerLines.isEmpty())
				bw.write(headerLines);

			// Write the header line of a Hapmap file
			bw.write("rs#\talleles\tchrom\tpos\tstrand\tassembly#\tcenter\tprotLSID\tassayLSID\tpanelLSID\tQCcode");
			for (String line : lines)
				bw.write("\t" + line);
			bw.newLine();

			s = System.currentTimeMillis();

			int counter = 0;
			for (String markerName : markers) {
				MarkerPosition mp = map.get(markerName);

				if (mp == null)
					mp = new MarkerPosition("", "");

				bw.write(markerName + "\tNA\t" + mp.chromosome + "\t" + mp.position + "\tNA\tNA\tNA\tNA\tNA\tNA\tNA");

				// Read in a marker row (all of its alleles from file)
				// Get from DATA, lineInds.size(), 1 column, start from row 0 and column markerInds.get(markerName).
				// The resulting 2d array only contains one 1d array. Take that as the marker genotype data.
				byte[][] g = reader.int8().readMatrixBlock(DATA, hdf5Lines.size(), 1, 0, markerInds.get(markerName));
				byte[] genotypes = new byte[g.length];
				for (int i = 0; i < g.length; i++)
					genotypes[i] = g[i][0];

				for(Integer index : lineIndices) {
					String state = stateTable[genotypes[index]];

					if (StringUtils.isEmpty(state))
						state = "N";

					// Replace slashes
					if (state.contains("/"))
						state = state.replace("/", "");

					// Duplicate individual nucleotides
					if (state.length() == 1)
						state = state + state;

					bw.write("\t" + state);
				}

				bw.newLine();

				if (counter++ > 1_000_000) {
					bw.flush();
					counter = 0;
				}
			}
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
