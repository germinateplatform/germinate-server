package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableExportResource extends GermplasmBaseResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<?> from = getGermplasmQuery(context);

			// Filter here!
			filter(from, adjustFilter(filters));

			return export(from.fetch(), "germplasm-table-");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
