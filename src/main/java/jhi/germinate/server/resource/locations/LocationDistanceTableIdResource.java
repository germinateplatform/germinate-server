package jhi.germinate.server.resource.locations;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedLocationRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

/**
 * @author Sebastian Raubach
 */
public class LocationDistanceTableIdResource extends PaginatedServerResource
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
			SelectJoinStep<? extends Record> from = context.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_ID)
														   .from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull()
															  .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
