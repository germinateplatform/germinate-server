package jhi.germinate.server.resource.attributes;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.util.*;

import jhi.germinate.resource.*;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.ViewTableGermplasmAttributes.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmAttributeTableExportResource extends PaginatedServerResource
{
	private Integer germplasmId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.germplasmId = Integer.parseInt(getRequestAttributes().get("germplasmId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		if (germplasmId != null)
		{
			List<Filter> filter = new ArrayList<>();
			Filter[] oldFilter = request.getFilter();

			boolean found = false;
			if (oldFilter != null) {
				filter.addAll(Arrays.asList(oldFilter));
				for (Filter f : filter)
				{
					if (Objects.equals(f.getColumn(), "germplasmId"))
					{
						f.setValues(new String[]{Integer.toString(germplasmId)});
						found = true;
					}
				}
			}

			if (!found)
			{
				// If no id was requested, add a single filter asking for the id
				filter.add(new Filter("germplasmId", "equals", "and", new String[]{Integer.toString(germplasmId)}));
			}
			request.setFilter(filter.toArray(new Filter[0]));
		}

		processRequest(request);

		return export(VIEW_TABLE_GERMPLASM_ATTRIBUTES, "germplasm-attributes-table-");
	}
}
