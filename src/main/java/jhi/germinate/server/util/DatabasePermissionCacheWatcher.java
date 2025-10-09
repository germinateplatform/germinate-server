package jhi.germinate.server.util;

import jhi.germinate.server.AuthorizationFilter;
import org.apache.commons.io.monitor.*;

import java.io.*;

public class DatabasePermissionCacheWatcher
{
	private static FileAlterationMonitor monitor;
	private static File                  watchFile = null;

	/**
	 * Attempts to reads the properties file and then checks the required properties.
	 */
	public static void initialize(File folder)
	{
		try
		{
			// Create/update initially
			watchFile = new File(folder, "dbupdate.info");
			FileUtils.touch(watchFile.toPath());

			// Then watch file for changes
			FileAlterationObserver observer = FileAlterationObserver.builder().setFile(folder).get();
			monitor = new FileAlterationMonitor(1000L);
			observer.addListener(new FileAlterationListenerAdaptor()
			{
				@Override
				public void onFileChange(File file)
				{
					if (file.equals(watchFile))
					{
						// The trigger file has been changed/touched, update permissions
						AuthorizationFilter.refreshUserDatasetInfo(true);
					}
				}
			});
			monitor.addObserver(observer);
			monitor.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void stopFileWatcher()
	{
		try
		{
			if (monitor != null)
				monitor.stop();

			monitor = null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
