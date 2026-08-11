package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.ApplicationListener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.*;

/**
 * @author Sebastian Raubach
 */
public interface AsyncResource
{
	default void cancelJob(String uuid, String jobId)
	{
		try
		{
			ApplicationListener.SCHEDULER.cancelJob(jobId);

			File asyncFolder = ResourceUtils.getFromExternal(uuid, "async");

			if (asyncFolder.exists() && asyncFolder.isDirectory())
			{
				FileUtils.deleteDirectory(asyncFolder);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
