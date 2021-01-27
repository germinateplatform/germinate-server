package jhi.germinate.server.resource;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.*;
import org.restlet.resource.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author Sebastian Raubach
 */
public class LogResource extends BaseServerResource
{
	public static final String PARAM_DATE = "date";

	private Date date;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			date = parseDate(getQueryValue(PARAM_DATE));
		}
		catch (Exception e)
		{
			date = new Date();
		}
	}

	@Get("application/zip")
	@MinUserType(UserType.ADMIN)
	public Representation getJson()
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
		}
		else
		{
			FileRepresentation representation = null;

			// Get the context path
			String base = ServletUtils.getRequest(getRequest()).getContextPath();

			if (StringUtils.isEmpty(base))
				base = "ROOT";

			// And the date
			String formattedDate = getFormattedDate(date);

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
					File zipFile = createTempFile(null, "log-" + formattedDate, ".zip", false);

					FileUtils.zipUp(zipFile, Collections.singletonList(file));

					Disposition disposition = new Disposition(Disposition.TYPE_ATTACHMENT);
					disposition.setFilename(zipFile.getName());
					representation = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
					representation.setSize(zipFile.length());
					representation.setDisposition(disposition);
					representation.setAutoDeleting(true);

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			return representation;
		}
	}
}
