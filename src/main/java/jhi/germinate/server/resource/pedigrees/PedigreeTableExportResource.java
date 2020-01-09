package jhi.germinate.server.resource.pedigrees;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.ViewTablePedigrees.*;

/**
 * @author Sebastian Raubach
 */
public class PedigreeTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		return export(VIEW_TABLE_PEDIGREES, "pedigree-table-", null);
	}
}
