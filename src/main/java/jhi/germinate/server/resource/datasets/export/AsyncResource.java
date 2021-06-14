package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.ApplicationListener;

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

			File asyncFolder = new File(ResourceUtils.getFromExternal(uuid, "async"), uuid);

			if (asyncFolder.exists() && asyncFolder.isDirectory())
			{
				Files.walk(asyncFolder.toPath())
					 .map(Path::toFile)
					 .sorted((o1, o2) -> -o1.compareTo(o2))
					 .forEach(File::delete);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
