package jhi.germinate.server.util.tasks;

import com.google.gson.Gson;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.database.pojo.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.importers.DataImportRunner;
import jhi.germinate.server.util.*;
import jhi.oddjob.JobInfo;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.DATA_IMPORT_JOBS;

public class DataAutoImportTask implements Runnable
{
	@Override
	public void run()
	{
		Logger.getLogger("").info("RUNNING DATA AUTO IMPORT TASK.");

		File baseFolder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "import");

		if (!baseFolder.exists() || !baseFolder.isDirectory())
		{
			// Nothing to do here. It either doesn't exist or isn't a folder
			return;
		}

		int counter = 0;

		File[] folders = baseFolder.listFiles(File::isDirectory);

		if (!CollectionUtils.isEmpty(folders))
		{
			Gson gson = new Gson();
			outer:
			for (File folder : folders)
			{
				File configFile = new File(folder, "config.json");

				if (!configFile.exists() || !configFile.isFile())
				{
					Logger.getLogger("").warning("NO CONFIG FILE FOUND FOR: " + folder.getAbsolutePath());
					continue;
				}

				AutoImportJsonConfig config = null;

				try (BufferedReader br = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8))
				{
					config = gson.fromJson(br, AutoImportJsonConfig.class);

					if (config == null || config.userId == null || CollectionUtils.isEmpty(config.templates))
					{
						Logger.getLogger("").warning("INVALID CONFIG FILE FOUND: " + configFile.getAbsolutePath());
						continue;
					}

					for (TemplateConfig template : config.templates)
					{
						if (template.type == null)
						{
							Logger.getLogger("").warning("INVALID TEMPLATE TYPE FOUND: '" + template.type + "' IN " + configFile.getAbsolutePath());
							continue outer;
						}
						if (template.type == DataImportJobsDatatype.genotype && template.orientation == null)
						{
							Logger.getLogger("").warning("INVALID GENOTYPE DATA ORIENTATION FOUND: '" + template.orientation + "' IN " + configFile.getAbsolutePath());
							continue outer;
						}
						File f = new File(folder, template.file);

						if (!f.exists() || !f.isFile())
						{
							Logger.getLogger("").warning("TEMPLATE FILE NOT FOUND: " + f.getAbsolutePath());
							continue outer;
						}
					}

					// All is good if we get here

					try (Connection conn = Database.getConnection())
					{
						DSLContext context = Database.getContext(conn);

						List<TemplateConfig> germplasm = config.templates.stream().filter(t -> t.type == DataImportJobsDatatype.mcpd).sorted((a, b) -> Boolean.compare(a.isUpdate, b.isUpdate)).collect(Collectors.toList());

						for (TemplateConfig mcpdFile : germplasm)
						{
							SyncImportInfo info = processFile(context, folder, config.userId, mcpdFile);

							if (info != null && info.jobInfo != null)
							{
								while (!ApplicationListener.SCHEDULER.isJobFinished(info.jobInfo.getId()))
									Thread.sleep(1000);

								DataImportJobsRecord dataImportJobsRecord = context.selectFrom(DATA_IMPORT_JOBS).where(DATA_IMPORT_JOBS.ID.eq(info.dbJobId)).fetchAny();

								if (dataImportJobsRecord == null || dataImportJobsRecord.getStatus() != DataImportJobsStatus.completed)
								{
									Logger.getLogger("").warning("FAILED TO IMPORT TEMPLATE: " + mcpdFile);
									// TODO: Really return here?
									return;
								}
							}
						}
					}
				}
				catch (IOException | SQLException e)
				{
					e.printStackTrace();
					Logger.getLogger("").warning(e.getLocalizedMessage());
				}
				catch (Exception e)
				{
					// From job scheduler
					e.printStackTrace();
					Logger.getLogger("").warning(e.getLocalizedMessage());
				}
			}
		}
	}

	private static SyncImportInfo processFile(DSLContext context, File sourceFolder, Integer userId, TemplateConfig template)
			throws Exception
	{
		String uuid = UUID.randomUUID().toString();
		// Get the target folder for all generated files
		File asyncFolder = ResourceUtils.getFromExternal(null, uuid, "async");
		asyncFolder.mkdirs();
		String extension = template.file.substring(template.file.lastIndexOf(".") + 1);

		if (template.type == null)
			return null;

		File source = new File(sourceFolder, template.file);
		File target = new File(asyncFolder, uuid + "." + extension);

		Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

		ImportJobDetails details = new ImportJobDetails()
				.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
				.setDataFilename(target.getName())
				.setDeleteOnFail(true)
				.setTargetDatasetId(null)
				.setDataOrientation(template.orientation)
				.setRunType(RunType.CHECK_AND_IMPORT);

		// Store the job information in the database
		DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
		dbJob.setUuid(uuid);
		dbJob.setJobId("N/A");
		dbJob.setUserId(userId);
		dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		dbJob.setDatatype(template.type);
		dbJob.setOriginalFilename(source.getName());
		dbJob.setIsUpdate(template.isUpdate);
		dbJob.setDatasetstateId(2);
		dbJob.setStatus(DataImportJobsStatus.waiting);
		dbJob.setJobConfig(details);
		dbJob.store();

		String[] importerArgs = DataImportRunner.getImporterClassArgs(template.type, extension);
		List<String> args = DataImportRunner.getArgs(importerArgs, dbJob.getId());

		if (template.orientation != null && template.type == DataImportJobsDatatype.genotype)
		{
			args.add("-go");
			args.add(template.orientation.name());
		}

		JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, asyncFolder.getAbsolutePath());

		dbJob.setJobId(info.getId());
		dbJob.setStatus(DataImportJobsStatus.waiting);
		dbJob.setImported(true);
		dbJob.store();

		return new SyncImportInfo(info, dbJob.getId());
	}

	private static class AutoImportJsonConfig
	{
		private Integer              userId;
		private List<TemplateConfig> templates;
	}

	private static class TemplateConfig
	{
		private String                 file;
		private DataImportJobsDatatype type;
		private Boolean                isUpdate = false;
		private DataOrientation        orientation;

		@Override
		public String toString()
		{
			return "TemplateConfig{" +
					"file='" + file + '\'' +
					", type=" + type +
					", isUpdate=" + isUpdate +
					", orientation=" + orientation +
					'}';
		}
	}

	private static class SyncImportInfo
	{
		private JobInfo jobInfo;
		private Integer dbJobId;

		public SyncImportInfo(JobInfo jobInfo, Integer dbJobId)
		{
			this.jobInfo = jobInfo;
			this.dbJobId = dbJobId;
		}
	}
}
