package jhi.germinate.server.util;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.MapoverlaysReferenceTable;
import jhi.germinate.server.database.codegen.tables.records.MapoverlaysRecord;
import jhi.germinate.server.resource.images.ImageResource;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Mapoverlays.*;

public class DatabaseUpdateFixer
{
	/**
	 * Migrates any climate overlays to mapoverlays. This couldn't be done in an SQL script, since we need to move files on the filesystem.
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void migrateClimateOverlayImages()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<MapoverlaysRecord> overlays = context.selectFrom(MAPOVERLAYS)
													  .where(MAPOVERLAYS.REFERENCE_TABLE.eq(MapoverlaysReferenceTable.climates))
													  .fetchInto(MapoverlaysRecord.class);

			File climateFolder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.climate.name());
			File mapoverlayFolder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.mapoverlay.name());
			mapoverlayFolder.mkdirs();

			if  (climateFolder.exists() && climateFolder.isDirectory())
			{
				for (MapoverlaysRecord overlay : overlays)
				{
					File oldFile = new File(climateFolder, overlay.getName());
					File newFile = new File(mapoverlayFolder, overlay.getName());

					if (oldFile.exists())
					{
						// Copy to new location
						if (!newFile.exists())
							Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}

				FileUtils.deleteDirectory(climateFolder);
			}
		}
	}
}
