package jhi.germinate.server.resource.maps.writer;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.database.codegen.tables.pojos.Maps;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.Record;

import java.io.*;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Mapfeaturetypes.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class StrudelMapWriter extends AbstractMapWriter
{
	private Maps map;

	public StrudelMapWriter(BufferedWriter bw)
	{
		super(bw);
	}

	@Override
	public void writeHeader(Maps map)
		throws IOException
	{
		this.map = map;
	}

	@Override
	public void writeRow(Record record)
		throws IOException
	{
		String markerName = record.get(MARKERS.MARKER_NAME);
		String chromosome = record.get(MAPDEFINITIONS.CHROMOSOME);
		Double defStart = record.get(MAPDEFINITIONS.DEFINITION_START);
		Double defEnd = record.get(MAPDEFINITIONS.DEFINITION_END);
		String mapFeat = record.get(MAPFEATURETYPES.DESCRIPTION);

		bw.write("feature\t" + map.getName() + "\t");
		bw.write(chromosome == null ? "" : chromosome);
		bw.write("\t");
		bw.write(markerName == null ? "" : markerName);
		bw.write("\t");
		bw.write(mapFeat == null ? "" : mapFeat);
		bw.write("\t");
		bw.write(defStart == null ? "" : Double.toString(defStart));
		bw.write("\t");
		bw.write(defEnd == null ? "" : Double.toString(defEnd));
		bw.write("\t");
		bw.write("Imported from Germinate!");
		bw.newLine();
	}

	@Override
	public void writeFooter()
		throws IOException
	{
		String serverBase = PropertyWatcher.get(ServerProperty.GERMINATE_CLIENT_URL);

		if (!StringUtils.isEmpty(serverBase))
		{
			if (serverBase.endsWith("/"))
				serverBase = serverBase.substring(0, serverBase.length() - 1);

			String url = serverBase + "/data/genotypes/maps/" + map.getId();

			bw.write("URL\t" + map.getName() + "\t" + url);
		}
	}
}
