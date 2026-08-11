package jhi.germinate.server.resource.mapoverlay;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapoverlays;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;

import java.io.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapoverlays.VIEW_TABLE_MAPOVERLAYS;

@Path("mapoverlay")
@Secured
@PermitAll
public class MapOverlayResource extends ContextResource
{
	@GET
	@Path("/{mapoverlayId:\\d+}/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public byte[] getImage(@PathParam("mapoverlayId") Integer mapoverlayId, @QueryParam("token") String token, @Context HttpServletResponse response)
			throws IOException, SQLException
	{
		if (mapoverlayId == null)
			throw new BadRequestException();

		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		// If it's not a template image, check the image token
		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token))
				throw new ForbiddenException();
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTableMapoverlays overlay = context.selectFrom(VIEW_TABLE_MAPOVERLAYS)
			                                      .where(VIEW_TABLE_MAPOVERLAYS.MAPOVERLAY_ID.eq(mapoverlayId))
			                                      .fetchAnyInto(ViewTableMapoverlays.class);

			if (overlay == null)
				throw new NotFoundException();

			// Check they have access to the dataset (if present)
			if (overlay.getDatasetId() != null)
			{
				List<Integer> ids = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true);

				if (!ids.contains(overlay.getDatasetId()))
					throw new NotFoundException();
			}

			File parent = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.mapoverlay.name());
			File image = new File(parent, overlay.getMapoverlayName());

			if (!image.exists() || !image.isFile())
				throw new NotFoundException();

			try
			{
				byte[] bytes = IOUtils.toByteArray(image.toURI());

				response.setContentType("image/png");

				return bytes;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new InternalServerErrorException();
			}
		}
	}
}
