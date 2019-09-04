package jhi.germinate.server.resource.maps;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.tables.Maps.*;
import static jhi.germinate.server.database.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class MapExportResource extends BaseServerResource
{
	private Integer mapId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.mapId = Integer.parseInt(getRequestAttributes().get("mapId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get()
	public FileRepresentation getFile()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		FileRepresentation representation;
		try
		{
			File file = createTempFile("map-" + mapId, ".tsv");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)))
			{
				bw.write("# fjFile = MAP");
				bw.newLine();

				context.selectFrom(MAPDEFINITIONS.leftJoin(MAPS).on(MAPS.ID.eq(MAPDEFINITIONS.MAP_ID))
												 .leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID)))
					   .where(MAPS.VISIBILITY.eq(true)
											 .or(MAPS.USER_ID.eq(userDetails.getId())))
					   .and(MAPS.ID.eq(mapId))
					   .orderBy(MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START, MARKERS.MARKER_NAME)
					   .stream()
					   .forEachOrdered(m -> {
						   try
						   {
							   String markerName = m.get(MARKERS.MARKER_NAME);
							   String chromosome = m.get(MAPDEFINITIONS.CHROMOSOME);
							   Double defStart = m.get(MAPDEFINITIONS.DEFINITION_START);

							   bw.write(markerName == null ? "" : markerName);
							   bw.write("\t");
							   bw.write(chromosome == null ? "" : chromosome);
							   bw.write("\t");
							   bw.write(defStart == null ? "" : Double.toString(defStart));
							   bw.newLine();
						   }
						   catch (IOException e)
						   {
							   e.printStackTrace();
						   }
					   });
			}
			catch (SQLException | IOException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
