package jhi.germinate.server.resource.datasets.export;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.tables.DatasetExportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class AsyncDatasetExportDownloadResource extends BaseServerResource
{
	private String jobUuid;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.jobUuid = getRequestAttributes().get("jobUuid").toString();

			try
			{
				// Check if it's a valid UUID
				UUID.fromString(this.jobUuid);
			}
			catch (IllegalArgumentException e)
			{
				this.jobUuid = null;
			}
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get
	public FileRepresentation getJson()
	{
		if (StringUtils.isEmpty(jobUuid))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS)
													.where(DATASET_EXPORT_JOBS.UUID.eq(jobUuid))
													.and(DATASET_EXPORT_JOBS.VISIBILITY.eq(true))
													.fetchAnyInto(DatasetExportJobsRecord.class);

			if (record == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			String uuid = record.getUuid();
			File jobFolder = getFromExternal(uuid, "async");

			// Get zip result files (there'll only be one per folder)
			File[] zipFiles = jobFolder.listFiles((dir, name) -> name.endsWith(".zip"));

			if (CollectionUtils.isEmpty(zipFiles))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			File resultFile = zipFiles[0];
			// Update this, so the file doesn't get deleted by the background async folder cleanup task
			resultFile.setLastModified(System.currentTimeMillis());

			record.setVisibility(false);
			record.store(DATASET_EXPORT_JOBS.VISIBILITY);

			FileRepresentation representation = new FileRepresentation(resultFile, MediaType.APPLICATION_ZIP);
			representation.setSize(resultFile.length());

			// TODO: Kick of deletion thread from here as well!
			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename(resultFile.getName());
			disp.setSize(resultFile.length());
			representation.setDisposition(disp);
			return representation;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
