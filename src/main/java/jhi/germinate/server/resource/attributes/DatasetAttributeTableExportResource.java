package jhi.germinate.server.resource.attributes;

import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.*;
import org.jooq.Condition;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
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
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postDatasetAttributeExport(ExportRequest request)
		throws IOException, SQLException
	{
		return export(null, request);
	}

	@POST
	@Path("/{datasetId}/attribute/export")
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postDatasetAttributeExport(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException
	{
		return export(datasetId, null);
	}

	private Response export(Integer datasetId, ExportRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

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

		requestedIds = AuthorizationFilter.restrictDatasetIds(req, null, requestedIds, true);

		// If either nothing is available or the user has access to nothing, return a 404
		if (CollectionUtils.isEmpty(requestedIds))
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_DATASET_ATTRIBUTES.DATASET_ID.in(requestedIds)};
		return export(VIEW_TABLE_DATASET_ATTRIBUTES, "dataset-attributes-table-", settings);
	}
}
