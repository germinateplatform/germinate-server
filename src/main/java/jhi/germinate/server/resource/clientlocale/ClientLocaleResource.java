package jhi.germinate.server.resource.clientlocale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jhi.germinate.resource.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.StringUtils;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static jhi.germinate.server.util.ApplicationListener.*;

@Path("clientlocale")
public class ClientLocaleResource extends ContextResource
{

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<LocaleConfig> getLocale()
		throws IOException
	{
		File configFile = ResourceUtils.getFromExternal(resp, "locales.json", "template");
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<LocaleConfig>>()
		{
		}.getType();

		if (configFile == null || !configFile.exists())
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}
		else
		{
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				return gson.fromJson(br, type);
			}
		}
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
			File file = getFromExternal(locale + ".json", "template");

			if (file.exists() && file.isFile())
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
