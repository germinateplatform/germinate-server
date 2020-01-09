package jhi.germinate.server.resource.germplasm;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.ViewTableGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		return export(VIEW_TABLE_GERMPLASM, "germplasm-table-", null);
	}
}
