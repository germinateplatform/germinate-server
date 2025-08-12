package jhi.germinate.server.resource.importers;

import jakarta.ws.rs.core.Response;
import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.database.pojo.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.importer.*;
import jhi.germinate.server.util.importer.cli.*;
import jhi.oddjob.JobInfo;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.DATA_IMPORT_JOBS;

/**
 * @author Sebastian Raubach
 */
public class DataImportRunner
{
	public List<AsyncExportResult> importData(String uuid)
			throws GerminateException, SQLException
	{
		DataImportMode mode = PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class);

		if (mode != DataImportMode.IMPORT)
			throw new GerminateException(Response.Status.SERVICE_UNAVAILABLE);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataImportJobsRecord record = context.selectFrom(DATA_IMPORT_JOBS)
												 .where(DATA_IMPORT_JOBS.UUID.eq(uuid))
												 .and(DATA_IMPORT_JOBS.STATUS.eq(DataImportJobsStatus.completed))
												 .and(DATA_IMPORT_JOBS.IMPORTED.eq(false))
												 // Check that there aren't any ERROR status types in there (all the ones that are there are WARNING).
												 .and(DSL.field("JSON_CONTAINS(" + DATA_IMPORT_JOBS.FEEDBACK.getName() + ", JSON_OBJECT('type', 'WARNING'))").eq(DSL.field("JSON_LENGTH(" + DATA_IMPORT_JOBS.FEEDBACK.getName() + ")")))
												 .fetchAnyInto(DataImportJobsRecord.class);

			if (record == null)
				throw new GerminateException(Response.Status.NOT_FOUND);

			// Replace the whole job details, because jOOQ will only execute the update if the job_config field changed, not fields within the JSON.
			record.setJobConfig(new ImportJobDetails()
					.setBaseFolder(record.getJobConfig().getBaseFolder())
					.setDataFilename(record.getJobConfig().getDataFilename())
					.setTargetDatasetId(record.getJobConfig().getTargetDatasetId())
					.setDataOrientation(record.getJobConfig().getDataOrientation())
					.setDeleteOnFail(record.getJobConfig().getDeleteOnFail())
					.setRunType(RunType.IMPORT));
			record.store();

			String originalFileName = record.getOriginalFilename();
			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

			File asyncFolder = ResourceUtils.getFromExternal(null, uuid, "async");

			String[] importerArgs = getImporterClassArgs(record.getDatatype(), extension);
			List<String> args = getArgs(importerArgs, record.getId());

			if (record.getJobConfig().getDataOrientation() != null && record.getDatatype() == DataImportJobsDatatype.genotype)
			{
				args.add("-go");
				args.add(record.getJobConfig().getDataOrientation().name());
			}

			JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, asyncFolder.getAbsolutePath());

			record.setJobId(info.getId());
			record.setStatus(DataImportJobsStatus.waiting);
			record.setImported(true);
			record.store();

			// Return the result
			AsyncExportResult individualResult = new AsyncExportResult();
			individualResult.setUuid(uuid);
			individualResult.setStatus("waiting");

			return Collections.singletonList(individualResult);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}
		return null;
	}

	public static List<AsyncExportResult> checkData(DataImportJobsDatatype dataType, AuthenticationFilter.UserDetails userDetails, String uuid, File templateFile, String originalFileName, boolean isUpdate, DataOrientation orientation, Integer datasetId, Integer datasetStateId)
			throws GerminateException
	{
		if (dataType == null)
			throw new GerminateException(Response.Status.BAD_REQUEST);

		DataImportMode mode = PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class);

		if (mode == DataImportMode.NONE)
			throw new GerminateException(Response.Status.SERVICE_UNAVAILABLE);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

			ImportJobDetails details = new ImportJobDetails()
					.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
					.setDataFilename(templateFile.getName())
					.setDeleteOnFail(true)
					.setTargetDatasetId(datasetId)
					.setDataOrientation(orientation)
					.setRunType(RunType.CHECK);

			// Store the job information in the database
			DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId("N/A");
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatatype(dataType);
			dbJob.setOriginalFilename(originalFileName);
			dbJob.setIsUpdate(isUpdate);
			dbJob.setDatasetstateId(datasetStateId);
			dbJob.setStatus(DataImportJobsStatus.waiting);
			dbJob.setJobConfig(details);
			if (userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			String[] importerArgs = getImporterClassArgs(dataType, extension);
			List<String> args = getArgs(importerArgs, dbJob.getId());

			if (orientation != null && dataType == DataImportJobsDatatype.genotype)
			{
				args.add("-go");
				args.add(orientation.name());
			}

			JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, templateFile.getParentFile().getAbsolutePath());

			// Update the job id
			dbJob.setJobId(info.getId());
			dbJob.store();

			// Return the result
			AsyncExportResult individualResult = new AsyncExportResult();
			individualResult.setUuid(uuid);
			individualResult.setStatus("waiting");

			return Collections.singletonList(individualResult);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	public static String[] getImporterClassArgs(DataImportJobsDatatype dataType, String extension)
			throws GerminateException
	{
		switch (dataType)
		{
			case mcpd:
				return McpdImporterCommand.CMD_ARGS;
			case trial:
				return TrialImporterCommand.CMD_ARGS;
			case genotype:
				if (Objects.equals(extension, "xlsx"))
					return ExcelGenotypeImporterCommand.CMD_ARGS;
				else if (Objects.equals(extension, "txt"))
					return FlatFileGenotypeImporterCommand.CMD_ARGS;
				else if (Objects.equals(extension, "hapmap"))
					return HapmapGenotypeImporterCommand.CMD_ARGS;
			case pedigree:
				return PedigreeImporterCommand.CMD_ARGS;
			case groups:
				return GroupImporterCommand.CMD_ARGS;
			case climate:
				return ClimateImporterCommand.CMD_ARGS;
			case images:
				return ImageImporterCommand.CMD_ARGS;
			case shapefile:
				return ShapefileImporterCommand.CMD_ARGS;
			case geotiff:
				return GeotiffImporterCommand.CMD_ARGS;
			default:
				throw new GerminateException(Response.Status.NOT_IMPLEMENTED);
				// TODO: Others
		}
	}

	public static List<String> getArgs(String[] importerClassArgs, Integer jobDbId)
			throws URISyntaxException
	{
		File libFolder = ResourceUtils.getLibFolder();
		String filename = "germinate-" + DataImportRunner.class.getPackage().getImplementationVersion() + ".jar";
		File germinateJar = new File(libFolder, filename);
		List<String> args = new ArrayList<>();
		// Select Germinate jar
		args.add("-jar");
		args.add(germinateJar.getAbsolutePath());
		// Add the command selection for the importer
		for (String importerClassArg : importerClassArgs)
			args.add(importerClassArg);
		// Add database parameters
		args.add("-dbserver");
		args.add(StringUtils.orEmptyQuotes( PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
		args.add("-dbname");
		args.add(StringUtils.orEmptyQuotes( PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
		args.add("-dbport");
		args.add(StringUtils.orEmptyQuotes( PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
		args.add("-dbuser");
		args.add(StringUtils.orEmptyQuotes( PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
		args.add("-dbpass");
		args.add(StringUtils.orEmptyQuotes( PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
		// Add the id
		args.add("-jid");
		args.add(jobDbId.toString());
		// This is an existing db job
		args.add("--existing-import-job");

		return args;
	}
}
