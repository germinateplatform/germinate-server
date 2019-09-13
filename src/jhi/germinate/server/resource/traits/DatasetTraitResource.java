package jhi.germinate.server.resource.traits;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableTraitDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTraitResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableTraitDatasets> getJson(DatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		String[] stringIds = requestedIds.stream()
										 .map(i -> Integer.toString(i))
										 .toArray(String[]::new);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record> from = context.select().from(VIEW_TABLE_TRAIT_DATASETS);

			// Filter here!
			Filter[] filters = new Filter[1];
			filters[0] = new Filter(VIEW_TABLE_TRAIT_DATASETS.DATASET_IDS.getName(), "arrayContains", "and", stringIds);
			filter(from, filters, true);

			return from.fetch()
					   .into(ViewTableTraitDatasets.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
