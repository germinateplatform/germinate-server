package jhi.germinate.server.resource.importers;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.enums.*;
import jhi.germinate.server.database.tables.records.DataImportJobsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.importer.McpdImporter;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.DataImportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class DataImporterResource extends BaseServerResource
{
	public static final String PARAM_IS_UPDATE = "update";
	public static final String PARAM_DATA_TYPE = "type";

	private boolean                isUpdate = false;
	private DataImportJobsDatatype dataType;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		String isUpdateString = getQueryValue(PARAM_IS_UPDATE);
		if (!StringUtils.isEmpty(isUpdateString))
			this.isUpdate = Boolean.parseBoolean(isUpdateString);

		try
		{
			this.dataType = DataImportJobsDatatype.valueOf(getQueryValue(PARAM_DATA_TYPE));
		}
		catch (Exception e)
		{
			this.dataType = null;
		}
	}

	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public List<AsyncExportResult> accept(Representation entity)
	{
		if (dataType == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		String importerClass = null;
		switch (dataType)
		{
			case mcpd:
				importerClass = McpdImporter.class.getCanonicalName();
				break;
			default:
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
				// TODO: Others
		}

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = getFromExternal(uuid, "async");
			asyncFolder.mkdirs();

			File file = new File(asyncFolder, uuid + ".xlsx");
			String originalFileName = FileUploadHandler.handle(entity, "fileToUpload", file);

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
			args.add(Boolean.toString(this.isUpdate)); // Update?
			args.add("true"); // Delete file if failed
			args.add("true"); // Only check, don't import

			ApplicationListener.SCHEDULER.initialize();
			String jobId = ApplicationListener.SCHEDULER.submit("java", args, asyncFolder.getAbsolutePath());

			// Store the job information in the database
			DataImportJobsRecord dbJob = context.newRecord(DATA_IMPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(jobId);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatatype(dataType);
			dbJob.setOriginalFilename(originalFileName);
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
}
