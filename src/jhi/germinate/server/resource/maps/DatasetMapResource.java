package jhi.germinate.server.resource.maps;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableDatasetMaps.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetMapResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableDatasetMaps>> getJson(DatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_DATASET_MAPS);

			from.where(VIEW_TABLE_DATASET_MAPS.VISIBILITY.eq(true)
														 .or(VIEW_TABLE_DATASET_MAPS.USER_ID.eq(userDetails.getId())))
				.and(VIEW_TABLE_DATASET_MAPS.DATASET_ID.in(requestedIds));

			// Filter here!
			filter(from, filters);

			List<ViewTableDatasetMaps> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableDatasetMaps.class);

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
