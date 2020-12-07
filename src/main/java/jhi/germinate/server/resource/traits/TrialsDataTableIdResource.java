package jhi.germinate.server.resource.traits;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableTrialsData.*;

/**
 * @author Sebastian Raubach
 */
public class TrialsDataTableIdResource extends PaginatedServerResource
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
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_TRIALS_DATA.GERMPLASM_ID)
														   .from(VIEW_TABLE_TRIALS_DATA);

			from.where(VIEW_TABLE_TRIALS_DATA.DATASET_ID.in(requestedIds));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
