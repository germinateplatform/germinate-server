package jhi.germinate.server.resource.images;

import org.apache.commons.io.IOUtils;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.*;

import java.io.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class ImageSvgSourceResource extends ServerResource
{
	public static final String PARAM_TOKEN = "token";

	private String name;
	private String token;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			name = getRequestAttributes().get("name").toString();
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		token = getQueryValue(PARAM_TOKEN);
	}

	@Get("image/svg+xml")
	public void getImage()
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !CustomVerifier.isValidImageToken(token))
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}

		if (!StringUtils.isEmpty(name))
		{
			File file = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), "template"), name);

			if (file.exists() && file.isFile())
			{
				try
				{
					byte[] bytes = IOUtils.toByteArray(file.toURI());
					ByteArrayRepresentation bar = new ByteArrayRepresentation(bytes, MediaType.IMAGE_SVG);
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
}
