package jhi.germinate.server.resource.maps.writer;

import org.jooq.Record;

import java.io.*;
import java.util.Objects;

import jhi.germinate.server.database.tables.pojos.*;

import static jhi.germinate.server.database.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class MapChartWriter extends AbstractMapWriter
{
	private String currentChromosome = "";

	public MapChartWriter(BufferedWriter bw)
	{
		super(bw);
	}

	@Override
	public void writeHeader(Maps map)
		throws IOException
	{
		bw.write("; " + map.getName());
		bw.newLine();
		bw.newLine();
	}

	@Override
	public void writeRow(Record record)
		throws IOException
	{
		String markerName = record.get(MARKERS.MARKER_NAME);
		String chromosome = record.get(MAPDEFINITIONS.CHROMOSOME);
		Double defStart = record.get(MAPDEFINITIONS.DEFINITION_START);

		if (!Objects.equals(chromosome, currentChromosome))
		{
			currentChromosome = chromosome;
			bw.write("group " + currentChromosome);
			bw.newLine();
		}
		bw.write(markerName == null ? "" : markerName);
		bw.write("\t");
		bw.write(defStart == null ? "" : Double.toString(defStart));
		bw.newLine();
	}

	@Override
	public void writeFooter()
		throws IOException
	{

	}
}
