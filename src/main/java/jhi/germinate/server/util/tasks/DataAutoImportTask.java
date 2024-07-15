package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.database.pojo.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.importers.DataImportRunner;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.importer.*;
import jhi.oddjob.JobInfo;
import org.jooq.DSLContext;

import java.io.File;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

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

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			for (ImportType type : ImportType.values())
			{
				File typeFolder = new File(baseFolder, type.name());

				if (typeFolder.exists() && typeFolder.isDirectory())
				{
					File[] files = typeFolder.listFiles();

					if (!CollectionUtils.isEmpty(files))
					{
						for (File f : files)
						{
							String uuid = UUID.randomUUID().toString();
							// Get the target folder for all generated files
							File asyncFolder = ResourceUtils.getFromExternal(null, uuid, "async");
							asyncFolder.mkdirs();
							String itemName = f.getName();
							String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
							File targetFile = new File(asyncFolder, uuid + "." + extension);

							Files.copy(f.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

							ImportJobDetails details = new ImportJobDetails()
									.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
									.setDataFilename(targetFile.getName())
									.setDeleteOnFail(true)
									.setTargetDatasetId(null)
									.setDataOrientation(DataOrientation.GENOTYPE_GERMPLASM_BY_MARKER)// TODO
									.setRunType(RunType.CHECK_AND_IMPORT);

							// Store the job information in the database
							DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
							dbJob.setUuid(uuid);
							dbJob.setJobId("N/A");
							dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
							dbJob.setDatatype(type.type);
							dbJob.setOriginalFilename(f.getName());
							dbJob.setIsUpdate(false); // TODO
							dbJob.setDatasetstateId(2);
							dbJob.setStatus(DataImportJobsStatus.waiting);
							dbJob.setJobConfig(details);
							dbJob.store();

							String importerClass = type.importerClassName;

							List<String> args = DataImportRunner.getArgs(importerClass, dbJob.getId());
							JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, asyncFolder.getAbsolutePath());

							dbJob.setJobId(info.getId());
							dbJob.setStatus(DataImportJobsStatus.waiting);
							dbJob.setImported(true);
							dbJob.store();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}

		Logger.getLogger("").info("DATA AUTO IMPORT TASK FINISHED WITH " + counter + " TASKS.");
	}

	private static enum ImportType
	{
		TRIAL(DataImportJobsDatatype.trial, TraitDataImporter.class.getCanonicalName()),
		PEDIGREE(DataImportJobsDatatype.pedigree, PedigreeImporter.class.getCanonicalName()),
		GROUPS(DataImportJobsDatatype.groups, GroupImporter.class.getCanonicalName()),
		CLIMATE(DataImportJobsDatatype.climate, ClimateDataImporter.class.getCanonicalName()),
		IMAGES(DataImportJobsDatatype.images, ImageImporter.class.getCanonicalName()),
		SHAPEFILE(DataImportJobsDatatype.shapefile, ShapefileImporter.class.getCanonicalName()),
		GEOTIFF(DataImportJobsDatatype.geotiff, GeotiffImporter.class.getCanonicalName());

		private DataImportJobsDatatype type;
		private String                 importerClassName;

		ImportType(DataImportJobsDatatype type, String importerClassName)
		{
			this.type = type;
			this.importerClassName = importerClassName;
		}
	}
}
