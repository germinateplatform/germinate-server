package jhi.germinate.server.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.tasks.*;
import jhi.oddjob.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

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
	// TODO: Init async scheduler based on config
	public static final IScheduler SCHEDULER = new ProcessScheduler();

	private static ScheduledExecutorService backgroundScheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.jooq.no-tips", "true");

		logMessage();

		try
		{
			SCHEDULER.initialize();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		PropertyWatcher.initialize();

		List<LocaleConfig> locales = ensureClientLocaleFileExists();
		ensureCarouselFileExists(locales);

		Long asyncDeleteDelay = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_ASYNC);
		Long tempDeleteDelay = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_TEMP);

		backgroundScheduler = Executors.newSingleThreadScheduledExecutor();
		// Every hour, update the dataset sizes
		backgroundScheduler.scheduleAtFixedRate(new DatasetMetaTask(), 1, 1, TimeUnit.HOURS);
		// Every minute, check the async job status
		backgroundScheduler.scheduleAtFixedRate(new DatasetExportJobCheckerTask(), 1, 15, TimeUnit.MINUTES);
		backgroundScheduler.scheduleAtFixedRate(new DatasetImportJobCheckerTask(), 1, 15, TimeUnit.MINUTES);
		backgroundScheduler.scheduleAtFixedRate(new ImageExifReaderTask(), 5, 1440, TimeUnit.MINUTES);
		backgroundScheduler.scheduleAtFixedRate(new UserFeedbackEmailRemovalTask(), 0, 1, TimeUnit.DAYS);

		// Every 5 minutes, get an update on the user information from Gatekeeper
		if (!StringUtils.isEmpty(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL)))
			backgroundScheduler.scheduleAtFixedRate(new GatekeeperUserUpdaterTask(), 1, 5, TimeUnit.MINUTES);

		// Every specified amount of hours, delete the async folders that aren't needed anymore
		if (asyncDeleteDelay != null)
			backgroundScheduler.scheduleAtFixedRate(new AsyncFolderCleanupTask(), 0, asyncDeleteDelay, TimeUnit.HOURS);

		// Every specified amount of hours, delete the temp files
		if (tempDeleteDelay != null)
			backgroundScheduler.scheduleAtFixedRate(new TempFolderCleanupTask(), 0, tempDeleteDelay, TimeUnit.HOURS);

		// Every 4 hours, update the PDCI
		if (PropertyWatcher.getBoolean(ServerProperty.PDCI_ENABLED))
			backgroundScheduler.scheduleAtFixedRate(new PDCITask(), 0, 4, TimeUnit.HOURS);
	}

	private void ensureCarouselFileExists(List<LocaleConfig> locales)
	{
		Gson gson = new Gson();
		try
		{
			File configFile = ResourceUtils.getFromExternal(null, "carousel.json", "template");
			Type type = new TypeToken<CarouselConfig>()
			{
			}.getType();

			if (configFile == null || !configFile.exists())
			{
				// Write a default file
				configFile.getParentFile().mkdirs();
				CarouselConfig config = new CarouselConfig();

				if (!CollectionUtils.isEmpty(locales))
					locales.forEach(l -> config.put(l.getLocale(), new ArrayList<>()));

				// Write the file back
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
				{
					gson.toJson(config, type, writer);
				}
			}
			else
			{
				try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
				{
					CarouselConfig config = gson.fromJson(br, type);

					if (config == null)
					{
						config = new CarouselConfig();

						if (!CollectionUtils.isEmpty(locales))
						{
							for (LocaleConfig l : locales)
							{
								config.put(l.getLocale(), new ArrayList<>());
							}
						}

						// Write the file back
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
						{
							gson.toJson(config, type, writer);
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}
	}

	private List<LocaleConfig> ensureClientLocaleFileExists()
	{
		Gson gson = new Gson();
		try
		{
			File configFile = ResourceUtils.getFromExternal(null, "locales.json", "template");
			Type type = new TypeToken<ArrayList<LocaleConfig>>()
			{
			}.getType();

			if (configFile == null || !configFile.exists())
			{
				// Write a default file
				configFile.getParentFile().mkdirs();
				ArrayList<LocaleConfig> config = new ArrayList<>();
				config.add(new LocaleConfig()
					.setLocale("en_GB")
					.setName("British English")
					.setFlag("gb"));

				// Write the file back
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
				{
					gson.toJson(config, type, writer);
				}

				return config;
			}
			else
			{
				try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
				{
					ArrayList<LocaleConfig> config = gson.fromJson(br, type);

					if (CollectionUtils.isEmpty(config))
					{
						config.add(new LocaleConfig()
							.setLocale("en_GB")
							.setName("British English")
							.setFlag("gb"));

						// Write the file back
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
						{
							gson.toJson(config, type, writer);
						}
					}

					return config;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}

		return null;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		PropertyWatcher.stopFileWatcher();
		GatekeeperClient.close();

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

		Database.close();
	}

	/**
	 * Returns the file with the given name from the external data folder in the given sub directory structure
	 *
	 * @param filename The name of the file to return
	 * @param subdirs  Optional sub-directory structure
	 * @return The {@link File} representing the request
	 */
	public static File getFromExternal(String filename, String... subdirs)
		throws IOException
	{
		File folder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL));

		if (subdirs != null)
		{
			for (String subdir : subdirs)
			{
				folder = new File(folder, subdir);
			}
		}

		File result = new File(folder, filename);

		if (!FileUtils.isSubDirectory(folder, result))
			throw new IOException("Invalid file access");

		return result;
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
