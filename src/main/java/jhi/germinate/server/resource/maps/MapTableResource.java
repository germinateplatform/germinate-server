package jhi.germinate.server.resource.maps;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMaps;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMaps.*;

/**
 * @author Sebastian Raubach
 */
public class MapTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableMaps>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MAPS);

			from.where(VIEW_TABLE_MAPS.VISIBILITY.eq(true)
												 .or(VIEW_TABLE_MAPS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<ViewTableMaps> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableMaps.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
