package jhi.germinate.server.resource.mapoverlay;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapoverlays;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;

import java.io.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapoverlays.*;

@Path("mapoverlay")
@Secured
@PermitAll
public class MapOverlayResource extends ContextResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Path("/{mapoverlayId:\\d+}/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImage(@PathParam("mapoverlayId") Integer mapoverlayId, @QueryParam("token") String token)
		throws IOException, SQLException
	{
		if (mapoverlayId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		// If it's not a template image, check the image token
		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTableMapoverlays overlay = context.selectFrom(VIEW_TABLE_MAPOVERLAYS)
												  .where(VIEW_TABLE_MAPOVERLAYS.MAPOVERLAY_ID.eq(mapoverlayId))
												  .fetchAnyInto(ViewTableMapoverlays.class);

			if (overlay == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			// Check they have access to the dataset (if present)
			if (overlay.getDatasetId() != null)
			{
				List<Integer> ids = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null, true);

				if (!ids.contains(overlay.getDatasetId()))
				{
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return null;
				}
			}

			File parent = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.mapoverlay.name());
			File image = new File(parent, overlay.getMapoverlayName());

			if (!image.exists() || !image.isFile())
				return Response.status(Response.Status.NOT_FOUND).build();

			try
			{
				byte[] bytes = IOUtils.toByteArray(image.toURI());

				return Response.ok(new ByteArrayInputStream(bytes))
							   .header("Content-Type", "image/png")
							   .build();
			}
			catch (IOException e)
			{
				e.printStackTrace();

				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return null;
			}
		}
	}
}
