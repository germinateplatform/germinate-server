package jhi.germinate.server;

import java.util.concurrent.*;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

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
		PropertyWatcher.initialize();
		// TODO: Init scheduler based on config

		backgroundScheduler = Executors.newSingleThreadScheduledExecutor();
		// Every hour, update the dataset sizes
		backgroundScheduler.scheduleAtFixedRate(new DatasetMetaTask(), 0, 1, TimeUnit.HOURS);
		// Every minute, check the async job status
		backgroundScheduler.scheduleAtFixedRate(new DatasetExportJobCheckerTask(), 0, 1, TimeUnit.MINUTES);
//		backgroundScheduler.scheduleAtFixedRate(new PDCIRunnable(), 0, 4, TimeUnit.HOURS);
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
			backgroundScheduler.shutdownNow();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
