package jhi.germinate.server.resource.climates;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.apache.commons.io.IOUtils;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.io.File;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Climateoverlays.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimateoverlays.*;

@Path("climate/overlay")
@Secured
@PermitAll
public class ClimateOverlayResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableClimateoverlays>> postClimateOverlay(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_CLIMATEOVERLAYS);

			// Filter here!
			filter(from, filters);

			List<ViewTableClimateoverlays> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableClimateoverlays.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@GET
	@Path("/{overlayId}/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getClimateOverlay(@PathParam("overlayId") Integer overlayId, @QueryParam("token") String token)
		throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}
		}

		if (overlayId == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Climateoverlays overlay = context.select()
											 .from(CLIMATEOVERLAYS)
											 .where(CLIMATEOVERLAYS.ID.eq(overlayId))
											 .fetchAnyInto(Climateoverlays.class);

			String name = overlay.getPath();

			if (!StringUtils.isEmpty(name))
			{
				File file = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), "climate"), name);

				if (file.exists() && file.isFile())
				{
					String extension = name.substring(name.lastIndexOf(".") + 1, name.length() - 1).toLowerCase();

					String mediaType;

					switch (extension)
					{
						case "jpg":
						case "jpeg":
							mediaType = "image/jpeg";
							break;
						case "png":
							mediaType = "image/png";
							break;
						case "svg":
							mediaType = "image/svg+xml";
							break;
						default:
							mediaType = "image/*";
					}

					try
					{
						byte[] bytes = IOUtils.toByteArray(file.toURI());

						return Response.ok(new ByteArrayInputStream(bytes))
									   .header("Content-Type", mediaType)
									   .build();
					}
					catch (IOException e)
					{
						e.printStackTrace();

						resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
						return null;
					}
				}
				else
				{
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return null;
				}
			}
			else
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return null;
			}
		}
	}
}
