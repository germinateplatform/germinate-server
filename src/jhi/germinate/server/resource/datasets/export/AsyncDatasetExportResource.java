package jhi.germinate.server.resource.datasets.export;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.germinate.resource.DatasetAsyncJobRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.pojos.DatasetExportJobs;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.DatasetExportJobs.*;

/**
 * @author Sebastian Raubach
 */
public class AsyncDatasetExportResource extends ServerResource
{
	@Post("json")
	public List<DatasetExportJobs> postJson(DatasetAsyncJobRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.selectFrom(DATASET_EXPORT_JOBS)
				   .where(DATASET_EXPORT_JOBS.UUID.in(request.getUuids()))
				   .or(DATASET_EXPORT_JOBS.USER_ID.eq(userDetails.getId()))
				   .fetchInto(DatasetExportJobs.class);

			// TODO: Add json parsing to code generator
			// TODO: Add file size of final files into the metadata
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
