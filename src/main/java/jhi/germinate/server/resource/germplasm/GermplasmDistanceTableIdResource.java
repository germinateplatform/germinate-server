package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedLocationRequest;
import jhi.germinate.server.Database;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class GermplasmDistanceTableIdResource extends GermplasmBaseResource
{
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedLocationRequest request)
	{
		if (request.getLatitude() == null || request.getLongitude() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			SelectConditionStep<?> from = getGermplasmIdQuery(context)
				.where(DSL.field(LONGITUDE).isNotNull())
				.and(DSL.field(LATITUDE).isNotNull());

			// Filter here!
			filter(from, adjustFilter(filters));

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
