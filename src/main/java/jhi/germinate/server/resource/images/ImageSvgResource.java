package jhi.germinate.server.resource.images;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.*;
import org.apache.commons.io.IOUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;

@Path("image/src-svg/{name}")
public class ImageSvgResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("image/svg+xml")
	public byte[] getSvgImage(@PathParam("name") String name)
		throws IOException
	{
		if (!StringUtils.isEmpty(name))
		{
			File parent = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), "template");
			File file = new File(parent, name);

			if (!FileUtils.isSubDirectory(parent, file))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}

			if (file.exists() && file.isFile())
			{
				try
				{
					return IOUtils.toByteArray(file.toURI());
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new InternalServerErrorException();
				}
			}
			else
			{
				throw new NotFoundException();
			}
		}
		else
		{
			throw new BadRequestException();
		}
	}
}
