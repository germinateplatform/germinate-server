package jhi.germinate.server.resource.importers;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.enums.*;
import jhi.germinate.server.database.tables.records.DataImportJobsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.importer.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.DataImportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class DataImportRunner
{
	public static List<AsyncExportResult> importData(String uuid)
	{
		DataImportMode mode = PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class);

		if (mode != DataImportMode.IMPORT)
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			DataImportJobsRecord record = context.selectFrom(DATA_IMPORT_JOBS)
												 .where(DATA_IMPORT_JOBS.UUID.eq(uuid))
												 .and(DATA_IMPORT_JOBS.STATUS.eq(DataImportJobsStatus.completed))
												 .and(DATA_IMPORT_JOBS.IMPORTED.eq(false))
												 .and(DSL.field("JSON_LENGTH({0})", DATA_IMPORT_JOBS.FEEDBACK.getName()).eq(0))
												 .fetchAnyInto(DataImportJobsRecord.class);

			if (record == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			String originalFileName = record.getOriginalFilename();
			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
			String importerClass = getImporterClass(record.getDatatype(), extension);

			File asyncFolder = BaseServerResource.getFromExternal(uuid, "async");
			File file = new File(asyncFolder, uuid + "." + extension);

			List<String> args = getArgs(importerClass, file);
			args.add(Boolean.toString(record.getIsUpdate())); // Update?
			args.add("true"); // Delete file if failed
			args.add(AbstractImporter.RunType.IMPORT.name()); // Import straight away, no need to check it again
			args.add(Integer.toString(record.getUserId() == null ? -1 : record.getUserId())); // Add the user id

			String jobId = ApplicationListener.SCHEDULER.submit("java", args, asyncFolder.getAbsolutePath());

			record.setJobId(jobId);
			record.setStatus(DataImportJobsStatus.running);
			record.setImported(true);
			record.store();

			// Return the result
			AsyncExportResult individualResult = new AsyncExportResult();
			individualResult.setUuid(uuid);
			individualResult.setStatus("running");

			return Collections.singletonList(individualResult);
		}
		catch (Exception e)
		{

		}
		return null;
	}

	public static List<AsyncExportResult> checkData(DataImportJobsDatatype dataType, CustomVerifier.UserDetails userDetails, Representation entity, boolean isUpdate)
	{
		if (dataType == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		DataImportMode mode = PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class);

		if (mode == DataImportMode.NONE)
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = BaseServerResource.getFromExternal(uuid, "async");
			asyncFolder.mkdirs();

			String originalFileName = FileUploadHandler.handle(entity, "fileToUpload", asyncFolder, uuid);
			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
			File file = new File(asyncFolder, uuid + "." + extension);

			String importerClass = getImporterClass(dataType, extension);

			List<String> args = getArgs(importerClass, file);
			args.add(Boolean.toString(isUpdate)); // Update?
			args.add("true"); // Delete file if failed
			args.add(AbstractImporter.RunType.CHECK.name()); // Only check, don't import
			args.add(Integer.toString(userDetails.getId() == -1000 ? -1 : userDetails.getId())); // Add the user id

			String jobId = ApplicationListener.SCHEDULER.submit("java", args, asyncFolder.getAbsolutePath());

			// Store the job information in the database
			DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(jobId);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatatype(dataType);
			dbJob.setOriginalFilename(originalFileName);
			dbJob.setIsUpdate(isUpdate);
			dbJob.setStatus(DataImportJobsStatus.running);
			if (userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			// Return the result
			AsyncExportResult individualResult = new AsyncExportResult();
			individualResult.setUuid(uuid);
			individualResult.setStatus("running");

			return Collections.singletonList(individualResult);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private static String getImporterClass(DataImportJobsDatatype dataType, String extension)
	{
		switch (dataType)
		{
			case mcpd:
				return McpdImporter.class.getCanonicalName();
			case trial:
				return TraitDataImporter.class.getCanonicalName();
			case genotype:
				if (Objects.equals(extension, "xlsx"))
					return GenotypeImporter.class.getCanonicalName();
				else if (Objects.equals(extension, "txt"))
					return GenotypeFlatFileImporter.class.getCanonicalName();
			case compound:
				return CompoundDataImporter.class.getCanonicalName();
			case pedigree:
				return PedigreeImporter.class.getCanonicalName();
			case groups:
				return GroupImporter.class.getCanonicalName();
			default:
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
				// TODO: Others
		}
	}

	private static List<String> getArgs(String importerClass, File file)
		throws URISyntaxException
	{
		File libFolder = BaseServerResource.getLibFolder();
		List<String> args = new ArrayList<>();
		args.add("-cp");
		args.add(libFolder.getAbsolutePath() + File.separator + "*");
		args.add(importerClass);
		args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
		args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
		args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
		args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
		args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
		args.add(file.getAbsolutePath());
		return args;
	}
}
