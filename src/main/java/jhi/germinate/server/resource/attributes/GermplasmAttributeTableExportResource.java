package jhi.germinate.server.resource.attributes;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import static jhi.germinate.server.database.codegen.tables.ViewTableGermplasmAttributes.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmAttributeTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		return export(VIEW_TABLE_GERMPLASM_ATTRIBUTES, "germplasm-attributes-table-", null);
	}
}
