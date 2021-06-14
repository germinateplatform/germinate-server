package jhi.germinate.server.resource.clientlocale;

import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;

import static jhi.germinate.server.util.ApplicationListener.*;

@Path("clientlocale")
public class ClientLocaleResource extends ContextResource
{

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocale()
		throws IOException
	{
		return this.getLocale(null);
	}


	@Path("/{locale}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocale(@PathParam("locale") String locale)
		throws IOException
	{
		try
		{
			File file;

			if (StringUtils.isEmpty(locale))
			{
				file = getFromExternal("locales.json", "template");
			}
			else
			{
				file = getFromExternal(locale + ".json", "template");
			}

			if (file != null && file.exists() && file.isFile())
			{
				return Response.ok(file)
							   .type(MediaType.TEXT_PLAIN)
							   .header("content-length", file.length())
							   .build();
			}
			else
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}
	}
}
