package jhi.germinate.server.util.tasks;

import java.io.File;
import java.util.Arrays;
import java.util.logging.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class TempFolderCleanupTask implements Runnable
{
	private String path;

	public TempFolderCleanupTask(String path)
	{
		this.path = path;
	}

	@Override
	public void run()
	{
		File tempFolder = new File(System.getProperty("java.io.tmpdir"), path);
		Long keepFilesFor = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_TEMP);

		if (!tempFolder.exists() || !tempFolder.isDirectory() || keepFilesFor == null)
		{
			Logger.getLogger("").log(Level.SEVERE, "No temp deletion deadline set! Aborting TempFolderCleanupTask.");
			return;
		}
		else
		{
			Logger.getLogger("").log(Level.INFO, "Running TempFolderCleanupTask");
		}

		File[] files = tempFolder.listFiles();

		if (files != null)
		{
			Arrays.stream(files)
				  .filter(File::isFile)
				  .forEach(f -> {
					  long timestamp = f.lastModified();
					  if ((System.currentTimeMillis() - timestamp) > (keepFilesFor * 60 * 60 * 1000))
						  f.delete();
				  });
		}
	}
}
