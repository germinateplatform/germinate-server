package jhi.germinate.server.resource.settings;

import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;
import java.net.URISyntaxException;

import jhi.germinate.server.resource.ClientLocaleResource;

/**
 * @author Sebastian Raubach
 */
public class SettingsFileResource extends ServerResource
{
	public static final String PARAM_FILE_TYPE = "file-type";

	private String type = null;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		type = getQueryValue(PARAM_FILE_TYPE);
	}

	@Get("json")
	public FileRepresentation getJson()
	{
		switch (type)
		{
			case "carousel":
				return getFile("carousel.json");
			default:
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}

	private FileRepresentation getFile(String name)
	{
		try
		{
			// TODO: Get this from the external data directory
			File file = new File(ClientLocaleResource.class.getClassLoader().getResource(name).toURI());

			if (file.exists() && file.isFile())
			{
				FileRepresentation representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
				representation.setSize(file.length());
				representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
				return representation;
			}
			else
			{
				throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			}
		}
		catch (URISyntaxException | NullPointerException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
