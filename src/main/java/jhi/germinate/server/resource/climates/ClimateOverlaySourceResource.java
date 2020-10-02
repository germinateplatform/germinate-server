package jhi.germinate.server.resource.climates;

import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.sql.*;
import java.util.logging.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.Climateoverlays;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.codegen.tables.Climateoverlays.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateOverlaySourceResource extends ServerResource
{
	public static final String PARAM_TOKEN = "token";

	private Integer overlayId;

	private String token;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			overlayId = Integer.parseInt(getRequestAttributes().get("overlayId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}
		token = getQueryValue(PARAM_TOKEN);
	}

	@Get("image/*")
	public void getImage()
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		Logger.getLogger("").log(Level.INFO, overlayId + " " + token);

		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !CustomVerifier.isValidImageToken(token))
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}

		if (overlayId == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			Climateoverlays overlay = context.select()
											 .from(CLIMATEOVERLAYS)
											 .where(CLIMATEOVERLAYS.ID.eq(overlayId))
											 .fetchAnyInto(Climateoverlays.class);

			String name = overlay.getPath();

			if (!StringUtils.isEmpty(name))
			{
				File file = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), "climate"), name);

				String extension = name.substring(name.lastIndexOf(".") + 1, name.length() - 1).toLowerCase();

				MediaType mediaType;

				switch (extension)
				{
					case "jpg":
					case "jpeg":
						mediaType = MediaType.IMAGE_JPEG;
						break;
					case "png":
						mediaType = MediaType.IMAGE_PNG;
						break;
					case "svg":
						mediaType = MediaType.IMAGE_SVG;
						break;
					default:
						mediaType = MediaType.IMAGE_ALL;
				}

				if (file.exists() && file.isFile())
				{
					try
					{
						byte[] bytes = IOUtils.toByteArray(file.toURI());
						ByteArrayRepresentation bar = new ByteArrayRepresentation(bytes, mediaType);
						getResponse().setEntity(bar);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
					}
				}
				else
				{
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				}
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
