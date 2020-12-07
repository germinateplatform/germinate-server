package jhi.germinate.server.resource.locations;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

/**
 * @author Sebastian Raubach
 */
public class LocationDistanceTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<LocationDistance>> getJson(PaginatedLocationRequest request)
	{
		if (request.getLatitude() == null || request.getLongitude() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<? extends Record> select = context.select(
				DSL.asterisk(),
				DSL.cast(
					DSL.acos(
						DSL.sin(
							DSL.rad(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE))
						   .times(
							   DSL.sin(
								   DSL.rad(request.getLatitude())))
						   .plus(
							   DSL.cos(
								   DSL.rad(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE))
								  .times(
									  DSL.cos(
										  DSL.rad(request.getLatitude())))
								  .times(
									  DSL.cos(
										  DSL.rad(request.getLongitude())
											 .minus(
												 DSL.rad(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE))))))
					   .times(6378.7), Double.class).as("distance")
			);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<? extends Record> from = select.from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull()
															  .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()));

			// Filter here!
			filter(from, filters);

			List<LocationDistance> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(LocationDistance.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
