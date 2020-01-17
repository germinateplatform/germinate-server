package jhi.germinate.server.resource.images;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.util.Objects;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class ImageSourceResource extends ServerResource
{
	public static final String PARAM_IMAGE_TYPE = "type";
	public static final String PARAM_NAME       = "name";
	public static final String PARAM_SIZE       = "size";
	public static final String PARAM_TOKEN      = "token";

	private Integer imageId;

	private String type;
	private String name;
	private String size = "small";
	private String token;

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
		size = getQueryValue(PARAM_SIZE);
		token = getQueryValue(PARAM_TOKEN);
	}

	@Get("image/*")
	public void getImage()
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !CustomVerifier.isValidImageToken(token))
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}

		if (imageId != null)
		{
			// TODO
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		}
		else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(type))
		{
			File large = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), type), name);
			File small = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), type), "thumbnail-" + name);

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

			if (large.exists() && large.isFile())
			{
				if (!small.exists() && !Objects.equals(type, "template"))
				{
					try
					{
						Thumbnails.of(large)
								  .height(500)
								  .keepAspectRatio(true)
								  .toFile(small);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				File file;

				if (StringUtils.isEmpty(size) || size.equals("large"))
					file = large;
				else
					file = small;

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
}
