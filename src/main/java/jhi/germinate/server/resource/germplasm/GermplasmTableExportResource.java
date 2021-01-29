package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import org.jooq.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableExportResource extends GermplasmBaseResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		try (DSLContext context = Database.getContext())
		{
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, null);

			// Filter here!
			filter(from, filters);

			return export(from.fetch(), "germplasm-table-");
		}
	}
}
