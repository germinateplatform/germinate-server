package jhi.germinate.server.resource.attributes;

import org.jooq.Condition;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.util.*;

import jhi.germinate.resource.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableDatasetAttributes.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetAttributeTableExportResource extends PaginatedServerResource
{
	private Integer datasetId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.datasetId = Integer.parseInt(getRequestAttributes().get("datasetId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		List<Integer> requestedIds = new ArrayList<>();

		if (datasetId != null)
		{
			requestedIds.add(datasetId);
		}
		else if (request.getFilter() != null)
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

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		// If nothing has been requested, return data for all datasets, else, use the requested ones that the user has access to
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = availableDatasets;
		else
			requestedIds.retainAll(availableDatasets);

		// If either nothing is available or the user has access to nothing, return a 404
		if (CollectionUtils.isEmpty(availableDatasets) || CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		processRequest(request);
		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[] {VIEW_TABLE_DATASET_ATTRIBUTES.DATASET_ID.in(requestedIds)};
		return export(VIEW_TABLE_DATASET_ATTRIBUTES, "dataset-attributes-table-", settings);
	}
}
