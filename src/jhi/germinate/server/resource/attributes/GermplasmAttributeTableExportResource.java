package jhi.germinate.server.resource.attributes;

import org.jooq.Condition;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import jhi.germinate.resource.PaginatedRequest;
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
		}
	}

	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		ExportSettings settings = null;

		if (germplasmId != null)
		{
			settings = new ExportSettings();
			settings.conditions = new Condition[]{VIEW_TABLE_GERMPLASM_ATTRIBUTES.GERMPLASM_ID.eq(germplasmId)};
		}
		return export(VIEW_TABLE_GERMPLASM_ATTRIBUTES, "germplasm-attributes-table-", settings);
	}
}
