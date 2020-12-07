package jhi.germinate.server.resource.maps.writer;

import jhi.germinate.server.database.codegen.tables.pojos.Maps;
import org.jooq.Record;

import java.io.*;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class FlapjackMapWriter extends AbstractMapWriter
{
	public FlapjackMapWriter(BufferedWriter bw)
	{
		super(bw);
	}

	@Override
	public void writeHeader(Maps map)
		throws IOException
	{
		bw.write("# fjFile = MAP");
		bw.write(CRLF);
	}

	@Override
	public void writeRow(Record record)
		throws IOException
	{
		String markerName = record.get(MARKERS.MARKER_NAME);
		String chromosome = record.get(MAPDEFINITIONS.CHROMOSOME);
		Double defStart = record.get(MAPDEFINITIONS.DEFINITION_START);

		bw.write(markerName == null ? "" : markerName);
		bw.write("\t");
		bw.write(chromosome == null ? "" : chromosome);
		bw.write("\t");
		bw.write(defStart == null ? "" : Double.toString(defStart));
		bw.write(CRLF);
	}

	@Override
	public void writeFooter()
		throws IOException
	{

	}
}
