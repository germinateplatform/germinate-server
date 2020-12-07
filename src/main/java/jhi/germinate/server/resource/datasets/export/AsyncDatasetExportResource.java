package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.resource.UuidRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.DatasetExportJobs;
import jhi.germinate.server.database.codegen.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class AsyncDatasetExportResource extends BaseServerResource implements AsyncResource
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

	@Delete("json")
	@FreeForAll
	public boolean deleteJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (StringUtils.isEmpty(jobUuid))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS)
													.where(DATASET_EXPORT_JOBS.UUID.in(jobUuid))
													.fetchAnyInto(DatasetExportJobsRecord.class);

			boolean isCancelRequest = record.getStatus() == DatasetExportJobsStatus.running;

			// If the user is logged in
			if (userDetails.getId() != -1000)
			{
				if (Objects.equals(record.getUserId(), userDetails.getId()))
				{
					if (isCancelRequest)
					{
						record.setStatus(DatasetExportJobsStatus.cancelled);
						cancelJob(record.getUuid(), record.getJobId());
					}
					record.setVisibility(false);
					record.store();
					return true;
				}
				else
					throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN); // Otherwise, throw an exception
			}
			else
			{
				if (isCancelRequest)
				{
					record.setStatus(DatasetExportJobsStatus.cancelled);
					cancelJob(record.getUuid(), record.getJobId());
				}
				record.setVisibility(false);
				record.store();
				return true;
			}
		}
	}

	@Post("json")
	@FreeForAll
	public List<DatasetExportJobs> postJson(UuidRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			return new ArrayList<>();

		try (DSLContext context = Database.getContext())
		{
			return context.selectFrom(DATASET_EXPORT_JOBS)
						  .where(DATASET_EXPORT_JOBS.UUID.in(request.getUuids())
														 .or(DATASET_EXPORT_JOBS.USER_ID.eq(userDetails.getId())))
						  .and(DATASET_EXPORT_JOBS.VISIBILITY.eq(true))
						  .orderBy(DATASET_EXPORT_JOBS.UPDATED_ON.desc())
						  .fetchInto(DatasetExportJobs.class);
		}
	}
}
