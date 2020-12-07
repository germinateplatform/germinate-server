package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapdefinitions;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class MapMarkerDefinitionTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableMapdefinitions>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<ViewTableMapdefinitions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableMapdefinitions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
