package jhi.germinate.server.util.async;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.DataExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.DataExportJobsRecord;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableImages.*;

public class ImageZipExporter
{
	private static final SimpleDateFormat      SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private              File                  folder;
	private              File                  zipFile;
	private              DataExportJobs     exportJob;
	private              List<ViewTableImages> images;

	private final Instant start;

	public ImageZipExporter()
	{
		start = Instant.now();
	}

	public static void main(String[] args)
		throws IOException, SQLException
	{
		ImageZipExporter exporter = new ImageZipExporter();
		Database.init(args[0], args[1], args[2], args[3], args[4], false);
		Integer jobId = Integer.parseInt(args[5]);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			DataExportJobsRecord job = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			job.setStatus(DataExportJobsStatus.running);
			job.store(DATA_EXPORT_JOBS.STATUS);
			exporter.exportJob = job.into(DataExportJobs.class);
			exporter.folder = new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "async"), exporter.exportJob.getUuid());
			exporter.zipFile = new File(exporter.folder, exporter.folder.getName() + "-" + SDF.format(new Date()) + ".zip");

			exporter.run();

			DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			record.setStatus(DataExportJobsStatus.completed);
			record.setResultSize(exporter.zipFile.length());
			record.store(DATA_EXPORT_JOBS.STATUS, DATA_EXPORT_JOBS.RESULT_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
				record.setStatus(DataExportJobsStatus.failed);
				record.store(DATA_EXPORT_JOBS.STATUS);
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
				Logger.getLogger("").severe(ee.getMessage());
			}
		}
	}

	private void init()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the images
			if (exportJob.getJobConfig().getyIds() != null)
			{
				images = context.selectFrom(VIEW_TABLE_IMAGES)
								.where(VIEW_TABLE_IMAGES.IMAGE_ID.in(exportJob.getJobConfig().getyIds()))
								.fetchInto(ViewTableImages.class);

			}
		}
	}

	private void run()
		throws IOException, DataAccessException, SQLException
	{
		init();

		// Make sure it doesn't exist
		if (zipFile.exists())
			zipFile.delete();

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);
		URI uri = URI.create("jar:file:/" + prefix);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

		try (FileSystem fs = FileSystems.newFileSystem(uri, env, null))
		{
			File folder = new File(new File(exportJob.getJobConfig().getBaseFolder(), "images"), "database");
			images.forEach(i -> {
				File source = new File(folder, i.getImagePath());

				if (source.exists())
				{
					String targetPrefix = i.getImageRefTable();
					String targetName;
					if (i.getCreatedOn() != null)
						targetName = SDF.format(new Date(i.getCreatedOn().getTime())) + "-" + i.getReferenceName();
					else
						targetName = i.getReferenceName();
					String fileExtension = i.getImagePath().substring(i.getImagePath().lastIndexOf("."));
					java.nio.file.Path target = fs.getPath("/", targetPrefix, targetName + fileExtension);
					try
					{
						Files.createDirectories(target.getParent());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					int counter = 1;
					while (Files.exists(target))
					{
						String tempName = targetName + "-" + (counter++) + fileExtension;
						target = fs.getPath("/", targetPrefix, tempName);
					}

					try
					{
						Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			});
		}

		Duration duration = Duration.between(start, Instant.now());
		System.out.println("DURATION: " + duration);
	}
}