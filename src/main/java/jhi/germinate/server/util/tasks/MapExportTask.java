package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Maps;
import jhi.germinate.server.database.codegen.tables.records.MapsRecord;
import jhi.germinate.server.resource.maps.writer.*;
import jhi.germinate.server.util.ApplicationListener;
import org.jooq.*;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.MAPDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Mapfeaturetypes.MAPFEATURETYPES;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;

public class MapExportTask implements Runnable
{
	private Integer mapId;

	public MapExportTask()
	{
	}

	public MapExportTask(Integer mapId)
	{
		this.mapId = mapId;
	}

	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectWhereStep<MapsRecord> step = context.selectFrom(MAPS);

			if (mapId != null)
				step.where(MAPS.ID.eq(mapId));

			List<Maps> maps = step.fetchInto(Maps.class);

			Logger.getLogger("").info("RUNNING MAP EXPORT TASK FOR POTENTIAL MAPS: " + maps.size());

			for (Maps map : maps)
			{
				try
				{
					String mapId = Integer.toString(map.getId());
					File flapjackFile = ApplicationListener.getFromExternal("flapjack.tsv", "data", "maps", mapId);
					File strudelFile = ApplicationListener.getFromExternal("strudel.tsv", "data", "maps", mapId);
					File mapchartFile = ApplicationListener.getFromExternal("mapchart.tsv", "data", "maps", mapId);

					if (flapjackFile.exists())
						continue;

					flapjackFile.getParentFile().mkdirs();

					try (BufferedWriter bwF = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(flapjackFile), StandardCharsets.UTF_8));
						 BufferedWriter bwS = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(strudelFile), StandardCharsets.UTF_8));
						 BufferedWriter bwM = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapchartFile), StandardCharsets.UTF_8)))
					{
						Logger.getLogger("").info("EXPORTING MAP FILES FOR MAP " + mapId);

						AbstractMapWriter writerF = new FlapjackMapWriter(bwF);
						AbstractMapWriter writerS = new StrudelMapWriter(bwS);
						AbstractMapWriter writerM = new MapChartWriter(bwM);

						writerF.writeHeader(map);
						writerS.writeHeader(map);
						writerM.writeHeader(map);

						context.selectFrom(
									   MAPDEFINITIONS.leftJoin(MAPS).on(MAPS.ID.eq(MAPDEFINITIONS.MAP_ID))
													 .leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
													 .leftJoin(MAPFEATURETYPES).on(MAPFEATURETYPES.ID.eq(MAPDEFINITIONS.MAPFEATURETYPE_ID))
							   )

							   .where(MAPS.ID.eq(map.getId()))
							   .stream()
							   .forEachOrdered(m -> {
								   try
								   {
									   writerF.writeRow(m);
									   writerS.writeRow(m);
									   writerM.writeRow(m);
								   }
								   catch (IOException e)
								   {
									   Logger.getLogger("").severe(e.getMessage());
									   e.printStackTrace();
								   }
							   });

						writerF.writeFooter();
						writerS.writeFooter();
						writerM.writeFooter();
					}
					catch (IOException e)
					{
						Logger.getLogger("").severe(e.getMessage());
						e.printStackTrace();
					}
				}
				catch (IOException e)
				{
					Logger.getLogger("").severe(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger("").severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
