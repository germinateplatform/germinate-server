package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class MapMarkerDefinitionTableIdResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record1<Integer>> select = context.selectDistinct(VIEW_TABLE_MAPDEFINITIONS.MARKER_ID);

			SelectJoinStep<Record1<Integer>> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
