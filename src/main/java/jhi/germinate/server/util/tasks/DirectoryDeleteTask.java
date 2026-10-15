package jhi.germinate.server.util.tasks;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Tasks that attempts to recursively delete a directory.
 * On a failed attempt, it'll retry after <code>sleep</code> milliseconds and up to a number
 * of <code>maxAttempts</code> times.
 */
public class DirectoryDeleteTask implements Runnable
{
	private File directory;
	private int  maxAttempts;
	private int  sleep;

	public DirectoryDeleteTask(File directory, int maxAttempts, int sleep)
	{
		this.directory = directory;
		this.maxAttempts = maxAttempts;
		this.sleep = sleep;
	}

	@Override
	public void run()
	{
		int attempts = 0;
		boolean deleted = false;

		while (attempts < maxAttempts && !deleted && !Thread.currentThread().isInterrupted())
		{
			attempts++;
			try
			{
				if (!directory.exists())
				{
					deleted = true;
					break;
				}

				FileUtils.deleteDirectory(directory);
				deleted = true;
			}
			catch (IOException e)
			{
				if (attempts < maxAttempts)
				{
					try
					{
						Thread.sleep(sleep);
					}
					catch (InterruptedException ie)
					{
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		}

		if (!deleted && directory.exists())
		{
			Logger.getLogger("").severe("Failed to delete directory " + directory.getAbsolutePath() + " after " + maxAttempts + " attempts.");
		}
	}
}
