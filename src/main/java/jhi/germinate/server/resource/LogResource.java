package jhi.germinate.server.resource;

import jhi.germinate.resource.enums.*;
import jhi.germinate.server.util.*;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.*;

@Path("log")
@Secured({UserType.ADMIN})
public class LogResource extends ContextResource
{
	@Context
	ServletContext servletContext;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response getJson(@QueryParam("date") String dateString)
		throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			resp.sendError(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			return null;
		}
		else
		{
			try
			{
				Date date = DateTimeUtils.parseDate(dateString);

				// Get the context path
				String base = servletContext.getContextPath();

				if (StringUtils.isEmpty(base))
					base = "ROOT";

				// And the date
				String formattedDate = DateTimeUtils.getFormattedDate(date);

				// Put it together
				String logFile = base + "." + formattedDate + ".log";

				// Remove starting slashes
				if (logFile.startsWith("/"))
					logFile = logFile.substring(1);

				// And any internal slashes
				logFile = logFile.replace("/", "#");

				// Then get the file from the logs folder
				File file = new File(new File(System.getProperty("catalina.home"), "logs"), logFile);

				Logger.getLogger("").log(Level.INFO, "Getting log file: " + logFile);
				Logger.getLogger("").log(Level.INFO, "File resolves to: " + file.getAbsolutePath());

				// And check if it exists
				if (file.exists() && file.isFile())
				{
					try
					{
						File zipFile = ResourceUtils.createTempFile(null, "log-" + formattedDate, ".zip", false);

						FileUtils.zipUp(zipFile, Collections.singletonList(file));

						java.nio.file.Path zipFilePath = zipFile.toPath();
						return Response.ok((StreamingOutput) output -> {
							Files.copy(zipFilePath, output);
							Files.deleteIfExists(zipFilePath);
						})
									   .type("application/zip")
									   .header("content-disposition", "attachment;filename= \"" + zipFile.getName() + "\"")
									   .header("content-length", zipFile.length())
									   .build();

					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			catch (ParseException e)
			{
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}
}
