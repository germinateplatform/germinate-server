package jhi.germinate.server.resource.locations;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedPolygonRequest;
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
public class LocationPolygonTableIdResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedPolygonRequest request)
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_ID)
														   .from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()
															 .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
															 .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `view_table_locations`.`location_longitude`, ' ', `view_table_locations`.`location_latitude`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
