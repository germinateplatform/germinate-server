package jhi.germinate.server.util.watcher;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.*;
import java.util.logging.*;

public class SingleFileWatcher
{
	private static final Logger LOGGER = Logger.getLogger(SingleFileWatcher.class.getName());

	private final ScheduledExecutorService sharedScheduler;
	private final Path                     filePath;
	private final Runnable                 onChangeHandler;
	private final long                     pollIntervalMillis;

	private ScheduledFuture<?> scheduledTask;
	private FileTime           lastModified = null;
	private long               lastLength   = -1L;

	public SingleFileWatcher(ScheduledExecutorService sharedScheduler, File fileToWatch, Runnable onChangeHandler)
	{
		this(sharedScheduler, fileToWatch.toPath(), onChangeHandler, 2000L);
	}

	public SingleFileWatcher(ScheduledExecutorService sharedScheduler, Path filePath, Runnable onChangeHandler, long pollIntervalMillis)
	{
		this.sharedScheduler = sharedScheduler;
		this.filePath = filePath;
		this.onChangeHandler = onChangeHandler;
		this.pollIntervalMillis = pollIntervalMillis;
	}

	public synchronized void start()
	{
		if (scheduledTask != null && !scheduledTask.isCancelled())
		{
			return;
		}

		try
		{
			if (Files.exists(filePath))
			{
				lastModified = Files.getLastModifiedTime(filePath);
				lastLength = Files.size(filePath);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not initialize file watcher baseline for: " + filePath, e);
		}

		scheduledTask = sharedScheduler.scheduleAtFixedRate(() -> {
			try
			{
				if (!Files.exists(filePath))
				{
					return;
				}

				FileTime currentModified = Files.getLastModifiedTime(filePath);
				long currentLength = Files.size(filePath);

				// FileTime implements Comparable, so compareTo safely checks if it's newer
				boolean isNewer = lastModified == null || currentModified.compareTo(lastModified) > 0;
				boolean sizeChanged = currentLength != lastLength;

				// Trigger if timestamp advanced or if timestamp matches but file size changed
				if (isNewer || (lastModified != null && currentModified.equals(lastModified) && sizeChanged))
				{
					lastModified = currentModified;
					lastLength = currentLength;
					onChangeHandler.run();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Error checking file modification for: " + filePath, e);
			}
		}, 0, pollIntervalMillis, TimeUnit.MILLISECONDS);
	}

	public synchronized void stop()
	{
		if (scheduledTask != null)
		{
			scheduledTask.cancel(false);
			scheduledTask = null;
		}
	}
}