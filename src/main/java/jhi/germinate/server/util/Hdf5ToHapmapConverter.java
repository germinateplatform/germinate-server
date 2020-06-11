package jhi.germinate.server.util;

import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import ch.systemsx.cisd.hdf5.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.Markers;

import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class Hdf5ToHapmapConverter extends AbstractHdf5Converter
{
	private Map<String, MarkerPosition> map;

	public static void main(String[] args)
	{
		Database.init("localhost", "germinate_demo_api_utf8", "", "root", "", false);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			Set<String> lines = context.select(GERMINATEBASE.NAME).from(GERMINATEBASE).fetchSet(GERMINATEBASE.NAME);
			Set<String> markers = context.select(Markers.MARKERS.MARKER_NAME).from(MARKERS).fetchSet(Markers.MARKERS.MARKER_NAME);
			Map<String, MarkerPosition> map = new HashMap<>();

			context.selectFrom(VIEW_TABLE_MAPDEFINITIONS)
				   .forEach(m -> map.put(m.getMarkerName(), new MarkerPosition(m.getChromosome(), Integer.toString((int) Math.round(m.getPosition())))));

			File hdf5File = new File("D:\\germinate\\demo-api\\data\\genotypes\\genotypes-subset-1.hdf5");
			String outputFile = "d:/genotypes.hapmap";

			new Hdf5ToHapmapConverter(hdf5File, lines, markers, map, outputFile).extractData(null);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

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
		List<Integer> lineIndices = lines.parallelStream().map(line -> lineInds.get(line)).collect(Collectors.toList());
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
			writer.println(lines.parallelStream().collect(Collectors.joining("\t", "rs#\talleles\tchrom\tpos\tstrand\tassembly#\tcenter\tprotLSID\tassayLSID\tpanelLSID\tQCcode\t", "")));

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
