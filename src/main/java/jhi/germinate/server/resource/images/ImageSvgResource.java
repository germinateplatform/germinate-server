package jhi.germinate.server.resource.images;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.*;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;

@Path("image/src-svg/{name}")
public class ImageSvgResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("image/svg+xml")
	public Response getSvgImage(@PathParam("name") String name)
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
					byte[] bytes = IOUtils.toByteArray(file.toURI());

					return Response.ok(new ByteArrayInputStream(bytes))
								   .header("Content-Type", "image/svg+xml")
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
