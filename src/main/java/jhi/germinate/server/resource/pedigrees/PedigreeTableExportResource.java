package jhi.germinate.server.resource.pedigrees;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import static jhi.germinate.server.database.codegen.tables.ViewTablePedigrees.*;

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
