package jhi.germinate.server.resource.attributes;

import jhi.germinate.resource.*;
import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.Condition;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableDatasetAttributes.*;

@Path("dataset")
@Secured
@PermitAll
public class DatasetAttributeTableExportResource extends ExportResource
{
	@POST
	@Path("/attribute/table/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postDatasetAttributeExport(PaginatedRequest request)
		throws IOException, SQLException
	{
		return export(null, request);
	}

	@POST
	@Path("/{datasetId}/attribute/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postDatasetAttributeExport(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException
	{
		return export(datasetId, null);
	}

	private Response export(Integer datasetId, PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
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

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, null);

		// If nothing has been requested, return data for all datasets, else, use the requested ones that the user has access to
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = availableDatasets;
		else
			requestedIds.retainAll(availableDatasets);

		// If either nothing is available or the user has access to nothing, return a 404
		if (CollectionUtils.isEmpty(availableDatasets) || CollectionUtils.isEmpty(requestedIds))
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		processRequest(request);
		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_DATASET_ATTRIBUTES.DATASET_ID.in(requestedIds)};
		return export(VIEW_TABLE_DATASET_ATTRIBUTES, "dataset-attributes-table-", settings);
	}
}
