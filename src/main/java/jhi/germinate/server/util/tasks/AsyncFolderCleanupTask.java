package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.util.PropertyWatcher;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.DATA_EXPORT_JOBS;
import static jhi.germinate.server.database.codegen.tables.DataImportJobs.DATA_IMPORT_JOBS;

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
			Set<String> uuids = new HashSet<>();

			// Get all invisible jobs and failed jobs
			context.selectFrom(DATA_EXPORT_JOBS)
				   .forEach(j -> {
					   if (j.getVisibility() == false || j.getStatus() == DataExportJobsStatus.failed)
						   checkJob(j.getUuid());

					   uuids.add(j.getUuid());
				   });
			context.selectFrom(DATA_IMPORT_JOBS)
				   .forEach(j -> {
					   if (j.getVisibility() == false || j.getStatus() == DataImportJobsStatus.failed)
						   checkJob(j.getUuid());

					   uuids.add(j.getUuid());
				   });

			checkFolderForOrphans(uuids);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void checkFolderForOrphans(Set<String> validUuids)
	{
		try
		{
			Files.list(asyncFolder.toPath())
				 .map(Path::toFile)
				 .filter(f -> f.isDirectory() && !validUuids.contains(f.getName()))
				 .forEach(p -> {
					 Long timestamp = getLastModifiedForFolder(p);

					 if (timestamp != null && (System.currentTimeMillis() - timestamp) > (keepFilesFor * 60 * 60 * 1000))
					 {
						 Logger.getLogger("").log(Level.INFO, "Deleting orphaned async folder: " + p.getName());
						 try
						 {
							 FileUtils.forceDelete(p);
						 }
						 catch (IOException e)
						 {
							 e.printStackTrace();
						 }
					 }
				 });
		}
		catch (IOException e)
		{
			Logger.getLogger("").info(e.getLocalizedMessage());
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
				Logger.getLogger("").log(Level.INFO, "Deleting failed/invisible async folder: " + uuid);
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
