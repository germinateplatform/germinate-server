package jhi.germinate.server.resource.traits;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableTrialsData.*;

/**
 * @author Sebastian Raubach
 */
public class TrialsDataTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableTrialsData>> getJson(PaginatedDatasetRequest request)
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
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_TRIALS_DATA);

			from.where(VIEW_TABLE_TRIALS_DATA.DATASET_ID.in(requestedIds));

			// Filter here!
			filter(from, filters);

			List<ViewTableTrialsData> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableTrialsData.class);

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
