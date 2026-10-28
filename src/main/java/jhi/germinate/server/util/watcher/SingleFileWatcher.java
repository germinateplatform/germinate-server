package jhi.germinate.server.util.watcher;

import java.io.File;
import java.util.concurrent.*;
import java.util.logging.*;

public class SingleFileWatcher
{
	private static final Logger LOGGER = Logger.getLogger(SingleFileWatcher.class.getName());

	private final ScheduledExecutorService sharedScheduler;
	private final File                     fileToWatch;
	private final Runnable                 onChangeHandler;
	private final long                     pollIntervalMillis;

	private ScheduledFuture<?> scheduledTask;
	private long               lastModified = -1L;

	public SingleFileWatcher(ScheduledExecutorService sharedScheduler, File fileToWatch, Runnable onChangeHandler)
	{
		this(sharedScheduler, fileToWatch, onChangeHandler, 2000L); // Default: 2 seconds
	}

	public SingleFileWatcher(ScheduledExecutorService sharedScheduler, File fileToWatch, Runnable onChangeHandler, long pollIntervalMillis)
	{
		this.sharedScheduler = sharedScheduler;
		this.fileToWatch = fileToWatch;
		this.onChangeHandler = onChangeHandler;
		this.pollIntervalMillis = pollIntervalMillis;
	}

	public synchronized void start()
	{
		if (scheduledTask != null && !scheduledTask.isCancelled())
			return; // Already running

		if (fileToWatch.exists())
			lastModified = fileToWatch.lastModified();

		// Register the task onto your existing shared scheduler
		scheduledTask = sharedScheduler.scheduleAtFixedRate(() -> {
			try
			{
				if (!fileToWatch.exists())
					return;

				long currentModified = fileToWatch.lastModified();
				if (currentModified > lastModified)
				{
					lastModified = currentModified;
					onChangeHandler.run();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Error checking file modification for: " + fileToWatch.getAbsolutePath(), e);
			}
		}, 0, pollIntervalMillis, TimeUnit.MILLISECONDS);
	}

	public synchronized void stop()
	{
		if (scheduledTask != null)
		{
			// Cancel ONLY this specific file-watching task, keeping the shared pool alive
			scheduledTask.cancel(false);
			scheduledTask = null;
		}
	}
}