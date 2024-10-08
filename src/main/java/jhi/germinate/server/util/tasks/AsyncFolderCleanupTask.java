package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.util.PropertyWatcher;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.*;
import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class AsyncFolderCleanupTask implements Runnable
{
	private File asyncFolder;
	private Long keepFilesFor;

	@Override
	public void run()
	{
		this.asyncFolder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "async");
		this.keepFilesFor = PropertyWatcher.getLong(ServerProperty.FILES_DELETE_AFTER_HOURS_ASYNC);

		if (keepFilesFor == null)
		{
			Logger.getLogger("").log(Level.SEVERE, "No async deletion deadline set! Aborting AsyncFolderCleanupTask.");
			return;
		}
		else
		{
			Logger.getLogger("").log(Level.INFO, "Running AsyncFolderCleanupTask");
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Get all invisible jobs and failed jobs
			context.selectFrom(DATA_EXPORT_JOBS)
				   .where(DATA_EXPORT_JOBS.VISIBILITY.eq(false)
														.or(DATA_EXPORT_JOBS.STATUS.eq(DataExportJobsStatus.failed)))
				   .forEach(j -> checkJob(j.getUuid()));
			context.selectFrom(DATA_IMPORT_JOBS)
				   .where(DATA_IMPORT_JOBS.VISIBILITY.eq(false)
													 .or(DATA_IMPORT_JOBS.STATUS.eq(DataImportJobsStatus.failed)))
				   .forEach(j -> checkJob(j.getUuid()));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void checkJob(String uuid)
	{
		File jobFolder = new File(asyncFolder, uuid);

		if (jobFolder.exists() && jobFolder.isDirectory())
		{
			Long timestamp = getLastModifiedForFolder(jobFolder);

			if (timestamp != null && (System.currentTimeMillis() - timestamp) > (keepFilesFor * 60 * 60 * 1000))
			{
				Logger.getLogger("").log(Level.INFO, "Deleting async folder: " + uuid);
				try
				{
					FileUtils.forceDelete(jobFolder);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private Long getLastModifiedForFolder(File folder)
	{
		if (folder == null)
			return null;

		long result = folder.lastModified();
		File[] files = folder.listFiles();

		if (files != null)
		{
			return Arrays.stream(files)
						 .mapToLong(File::lastModified)
						 .max()
						 .orElse(result);
		}
		else
		{
			return result;
		}
	}
}
