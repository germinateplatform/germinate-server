package jhi.germinate.server.resource.climates;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.codegen.tables.ViewTableClimateData.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateDataTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableClimateData>> getJson(PaginatedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		// If nothing specific was requested, just return everything, else restrict to available datasets
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = datasets;
		else
			requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_CLIMATE_DATA);

			from.where(VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds));

			// Filter here!
			filter(from, filters);

			List<ViewTableClimateData> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableClimateData.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
