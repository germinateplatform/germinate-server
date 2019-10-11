package jhi.germinate.server.resource.attributes;

import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.util.*;
import java.util.logging.*;

import jhi.germinate.resource.*;
import jhi.germinate.resource.Filter;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;

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
		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		Logger.getLogger("").log(Level.INFO, "DATASET_IDS: " + availableDatasets.toString());

		List<Filter> filter = new ArrayList<>();
		Filter[] oldFilter = request.getFilter();
		if (oldFilter != null)
			filter.addAll(Arrays.asList(oldFilter));

		if (datasetId != null)
		{
			if (!availableDatasets.contains(datasetId))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			boolean found = false;
			if (oldFilter != null)
			{
				for (Filter f : filter)
				{
					if (Objects.equals(f.getColumn(), "datasetId"))
					{
						f.setValues(new String[]{Integer.toString(datasetId)});
						found = true;
					}
				}
			}

			if (!found)
			{
				// If no id was requested, add a single filter asking for the id
				filter.add(new Filter("datasetId", "equals", "and", new String[]{Integer.toString(datasetId)}));
			}
		}
		else
		{
			filter.stream()
				  .filter(f -> Objects.equals(f.getColumn(), "datasetId"))
				  .forEach(f -> {
					  // Get requested values
					  String[] values = f.getValues();
					  // Keep track of the ones that are allowed
					  List<String> acceptableValues = new ArrayList<>();
					  // For each requested
					  for (String v : values)
					  {
						  try
						  {
							  // Parse to int, then check if available. If so, add to acceptable
							  Integer id = Integer.parseInt(v);
							  if (availableDatasets.contains(id))
								  acceptableValues.add(v);
						  }
						  catch (Exception e)
						  {
						  }
					  }
					  // Update the values
					  f.setValues(acceptableValues.toArray(new String[0]));
				  });
		}

		request.setFilter(filter.toArray(new Filter[0]));
		processRequest(request);

		return export(VIEW_TABLE_DATASET_ATTRIBUTES, "dataset-attributes-table-");
	}
}
