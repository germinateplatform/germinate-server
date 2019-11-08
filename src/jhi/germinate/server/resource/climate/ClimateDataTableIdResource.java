package jhi.germinate.server.resource.climate;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableClimateData.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateDataTableIdResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		List<Integer> requestedIds =request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		// If nothing specific was requested, just return everything, else restrict to available datasets
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = datasets;
		else
			requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_CLIMATE_DATA.LOCATION_ID)
														   .from(VIEW_TABLE_CLIMATE_DATA);

			from.where(VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}