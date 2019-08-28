package jhi.germinate.server.resource.image;

import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class ImageSourceResource extends ServerResource
{
	public static final String PARAM_IMAGE_TYPE = "type";
	public static final String PARAM_NAME       = "name";

	private Integer imageId;

	private String type;
	private String name;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			imageId = Integer.parseInt(getRequestAttributes().get("imageId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		type = getQueryValue(PARAM_IMAGE_TYPE);
		name = getQueryValue(PARAM_NAME);
	}

	@Get
	public FileRepresentation getImage()
	{
		if (imageId != null)
		{
			// TODO
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		}
		else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(type))
		{
			File file = null;
			switch (type)
			{
				case "template":
					file = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), name);
					break;
			}

			if (file != null && file.exists() && file.isFile())
			{
				FileRepresentation representation = new FileRepresentation(file, MediaType.IMAGE_ALL);
				representation.setSize(file.length());
				representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
				return representation;
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
