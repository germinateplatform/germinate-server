package jhi.germinate.server;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.tasks.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import jhi.oddjob.*;

/**
 * The {@link ApplicationListener} is the main {@link ServletContextListener} of the application. It's started when the application is loaded by
 * Tomcat. It contains {@link #contextInitialized(ServletContextEvent)} which is executed on start and {@link #contextDestroyed(ServletContextEvent)}
 * which is executed when the application terminates.
 *
 * @author Sebastian Raubach
 */
@WebListener
public class ApplicationListener implements ServletContextListener
{
	public static final IScheduler SCHEDULER = new ProcessScheduler();

	private static ScheduledExecutorService backgroundScheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		logMessage();

		PropertyWatcher.initialize();
		// TODO: Init async scheduler based on config

		Long asyncDeleteDelay = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_ASYNC);
		Long tempDeleteDelay = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_TEMP);

		backgroundScheduler = Executors.newSingleThreadScheduledExecutor();
		// Every hour, update the dataset sizes
		backgroundScheduler.scheduleAtFixedRate(new DatasetMetaTask(), 0, 1, TimeUnit.HOURS);
		// Every minute, check the async job status
		backgroundScheduler.scheduleAtFixedRate(new DatasetExportJobCheckerTask(), 0, 1, TimeUnit.MINUTES);
		// Every specified amount of hours, delete the async folders that aren't needed anymore
		if (asyncDeleteDelay != null)
			backgroundScheduler.scheduleAtFixedRate(new AsyncFolderCleanupTask(), 0, asyncDeleteDelay, TimeUnit.HOURS);

		String path = sce.getServletContext().getContextPath();
		if (!StringUtils.isEmpty(path) && tempDeleteDelay != null)
		{
			String[] parts = path.split("/");
			String firstNonEmptyPart = Arrays.stream(parts)
											 .filter(p -> !StringUtils.isEmpty(p))
											 .findFirst()
											 .orElse(null);

			// Every specified amount of hours, delete the temp files
			if (!StringUtils.isEmpty(firstNonEmptyPart))
				backgroundScheduler.scheduleAtFixedRate(new TempFolderCleanupTask(firstNonEmptyPart), 0, tempDeleteDelay, TimeUnit.HOURS);
		}

		// Run automatic PDCI update if enabled
		if (PropertyWatcher.getBoolean(ServerProperty.PDCI_ENABLED))
			backgroundScheduler.scheduleAtFixedRate(new PDCITask(), 0, 4, TimeUnit.HOURS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		PropertyWatcher.stopFileWatcher();

		try
		{
			SCHEDULER.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			// Stop the scheduler
			if (backgroundScheduler != null)
				backgroundScheduler.shutdownNow();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void logMessage()
	{
		Logger.getLogger("").log(Level.INFO, "\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@#################%@@@@@@@////////%@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@#####################@@@@@@@#///////(@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@#######################@@@@@@@@////////@@@@@@@@@@\r\n" +
			"@@@@@@@@@((((((((@@@@@@@@@@@@@@@@@@@@@@@@@////////@@@@@@@@@\r\n" +
			"@@@@@@@@((((((((@@@@@@@@@@@@@@@@@@@@@@@@@@@////////@@@@@@@@\r\n" +
			"@@@@@@@((((((((@@@@@@@@@@@@@@@@@@@@@@@@@@@@@////////@@@@@@@\r\n" +
			"@@@@@&((((((((@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@////////@@@@@@\r\n" +
			"@@@@#(((((((/@@@@@@@@@@@@@@@@@@@@@@@((((((((((//////##%@@@@\r\n" +
			"@@@((((((((@@@@@@@@@@@@@@@@@@@@@@@@(((((((((((((///####%@@@\r\n" +
			"@@@(((((((@@@@@@@@@@@@@@@@@@@@@@@@(((((((((((((((/######@@@\r\n" +
			"@@@(((((((@@@@@@@@,,,,,,,,,,,,,,,*@@@@@@@@(((((((#######@@@\r\n" +
			"@@@((((((((@@@@@@@@,,,,,,,,,,,,,***@@@@@@@@(((((#######%@@@\r\n" +
			"@@@@/(((((((#@@@@@@@,,,,,,,,,,,*****@@@@@@@@(((#######%@@@@\r\n" +
			"@@@@@@((((((((@@@@@@@@@@@@@@@********@@@@@@@@########@@@@@@\r\n" +
			"@@@@@@@((((((((@@@@@@@@@@@@@********@@@@@@@@########@@@@@@@\r\n" +
			"@@@@@@@@((((((((@@@@@@@@@@@********@@@@@@@@########@@@@@@@@\r\n" +
			"@@@@@@@@@((((((((@@@@@@@@@********@@@@@@@@########@@@@@@@@@\r\n" +
			"@@@@@@@@@@((((((((///////////////@@@@@@@@########@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@((((((((////////////*@@@@@@@%#######%@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@(((((((//////////@@@@@@@@########%@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n" +
			"\r\n" +
			"               Thanks for using Germinate!\r\n");
	}
}
