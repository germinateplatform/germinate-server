package jhi.germinate.server.resource.clientlocale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletResponse;
import jhi.germinate.resource.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;

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
	@Produces(MediaType.APPLICATION_JSON)
	public List<LocaleConfig> getLocale()
		throws IOException, StatusException
	{
		File configFile = ResourceUtils.getFromExternal("locales.json", "template");
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<LocaleConfig>>()
		{
		}.getType();

		if (configFile == null || !configFile.exists())
			throw new NotFoundException();
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
	@Produces(MediaType.TEXT_PLAIN)
	public File getLocale(@PathParam("locale") String locale, @Context HttpServletResponse response)
		throws IOException
	{
		try
		{
			File file = getFromExternal(locale + ".json", "template");

			if (file.exists() && file.isFile())
			{
				return toFileResult(file, MediaType.TEXT_PLAIN, response);
			}
			else
			{
				throw new NotFoundException();
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			throw new NotFoundException();
		}
	}
}
