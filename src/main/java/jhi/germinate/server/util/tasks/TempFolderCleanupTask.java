package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.PropertyWatcher;

import java.io.File;
import java.util.Arrays;
import java.util.logging.*;

/**
 * @author Sebastian Raubach
 */
public class TempFolderCleanupTask implements Runnable
{
	@Override
	public void run()
	{
		String path = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
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
