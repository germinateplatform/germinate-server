package jhi.germinate.server.resource.attributes;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasetAttributes;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jooq.Record;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableDatasetAttributes.*;

@Path("dataset")
@Secured
@PermitAll
public class DatasetAttributeTableResource extends BaseResource implements IFilteredResource
{
	@POST
	@Path("/attribute/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasetAttributes>> postDatasetAttributeTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
		return export(null, request);
	}

	@POST
	@Path("/{datasetId}/attribute")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasetAttributes>> postDatasetAttributeTable(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException
	{
		return export(datasetId, null);
	}

	private PaginatedResult<List<ViewTableDatasetAttributes>> export(Integer datasetId, PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> requestedIds = new ArrayList<>();

		if (datasetId != null)
		{
			requestedIds.add(datasetId);
		}
		else if (request != null && request.getFilter() != null)
		{
			Filter matchingFilter = Arrays.stream(request.getFilter())
										  .filter(f -> f.getColumn().equals("datasetId"))
										  .findFirst()
										  .orElse(null);

			if (matchingFilter != null)
			{
				for (String value : matchingFilter.getValues())
				{
					try
					{
						requestedIds.add(Integer.parseInt(value));
					}
					catch (NumberFormatException e)
					{
					}
				}
			}
		}

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null);

		// If nothing has been requested, return data for all datasets, else, use the requested ones that the user has access to
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = availableDatasets;
		else
			requestedIds.retainAll(availableDatasets);

		// If either nothing is available or the user has access to nothing, return an empty result
		if (CollectionUtils.isEmpty(availableDatasets) || CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_DATASET_ATTRIBUTES)
													 .where(VIEW_TABLE_DATASET_ATTRIBUTES.DATASET_ID.in(requestedIds));

			// Filter here!
			where(from, filters);

			List<ViewTableDatasetAttributes> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableDatasetAttributes.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
