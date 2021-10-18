package jhi.germinate.server.resource.importers;

import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.importer.*;
import jhi.oddjob.JobInfo;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.*;

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
												 .and(DSL.field("JSON_LENGTH(" + DATA_IMPORT_JOBS.FEEDBACK.getName() + ")").eq(0))
												 .fetchAnyInto(DataImportJobsRecord.class);

			if (record == null)
				throw new GerminateException(Response.Status.NOT_FOUND);

			String originalFileName = record.getOriginalFilename();
			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
			String importerClass = getImporterClass(record.getDatatype(), extension);

			File asyncFolder = ResourceUtils.getFromExternal(uuid, "async");
			File file = new File(asyncFolder, uuid + "." + extension);

			List<String> args = getArgs(importerClass, file);
			args.add(Boolean.toString(record.getIsUpdate())); // Update?
			args.add("true"); // Delete file if failed
			args.add(AbstractImporter.RunType.IMPORT.name()); // Import straight away, no need to check it again
			args.add(Integer.toString(record.getUserId() == null ? -1 : record.getUserId())); // Add the user id
			args.add(record.getDatasetstateId() == null ? "1" : Integer.toString(record.getDatasetstateId()));

			if (record.getDatatype() == DataImportJobsDatatype.genotype)
			{
				File hdf5Folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "data"), "genotypes");
				hdf5Folder.mkdirs();
				args.add(hdf5Folder.getAbsolutePath());
			}

			JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, asyncFolder.getAbsolutePath());

			record.setJobId(info.getId());
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
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}
		return null;
	}

	public static List<AsyncExportResult> checkData(DataImportJobsDatatype dataType, AuthenticationFilter.UserDetails userDetails, HttpServletRequest req, boolean isUpdate, Integer datasetStateId)
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
			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = ResourceUtils.getFromExternal(uuid, "async");
			asyncFolder.mkdirs();

			String originalFileName = FileUploadHandler.handle(req, "fileToUpload", asyncFolder, uuid);
			String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
			File file = new File(asyncFolder, uuid + "." + extension);

			String importerClass = getImporterClass(dataType, extension);

			List<String> args = getArgs(importerClass, file);
			args.add(Boolean.toString(isUpdate)); // Update?
			args.add("true"); // Delete file if failed
			args.add(AbstractImporter.RunType.CHECK.name()); // Only check, don't import
			args.add(Integer.toString(userDetails.getId() == -1000 ? -1 : userDetails.getId())); // Add the user id
			args.add(datasetStateId == null ? "1" : Integer.toString(datasetStateId));

			if (dataType == DataImportJobsDatatype.genotype)
			{
				File hdf5Folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "data"), "genotypes");
				hdf5Folder.mkdirs();
				args.add(hdf5Folder.getAbsolutePath());
			}

			JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateDataImportJob", "java", args, asyncFolder.getAbsolutePath());

			// Store the job information in the database
			DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(info.getId());
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatatype(dataType);
			dbJob.setOriginalFilename(originalFileName);
			dbJob.setIsUpdate(isUpdate);
			dbJob.setDatasetstateId(datasetStateId);
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
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private static String getImporterClass(DataImportJobsDatatype dataType, String extension)
		throws GerminateException
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
			case climate:
				return ClimateDataImporter.class.getCanonicalName();
			default:
				throw new GerminateException(Response.Status.NOT_IMPLEMENTED);
				// TODO: Others
		}
	}

	private static List<String> getArgs(String importerClass, File file)
		throws URISyntaxException
	{
		File libFolder = ResourceUtils.getLibFolder();
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
