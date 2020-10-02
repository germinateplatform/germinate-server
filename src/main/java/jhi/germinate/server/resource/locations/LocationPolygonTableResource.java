package jhi.germinate.server.resource.locations;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

/**
 * @author Sebastian Raubach
 */
public class LocationPolygonTableResource extends PaginatedServerResource
{
	public static String buildSqlPolygon(LatLng[][] points)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("MULTIPOLYGON(");

		if (!CollectionUtils.isEmpty(points))
		{
			builder.append("((");

			List<List<LatLng>> bounds = Arrays.stream(points)
											  .map(p -> new ArrayList<>(Arrays.asList(p)))
											  .collect(Collectors.toList());

			// Add the start as end point
			bounds.forEach(l -> l.add(l.get(0)));

			builder.append(bounds.stream()
								 .map(p -> p.stream().map(l -> l.getLng() + " " + l.getLat()).collect(Collectors.joining(", ")))
								 .collect(Collectors.joining(")), ((")));

			builder.append("))");
		}

		builder.append(")");

		return builder.toString();
	}

	@Post("json")
	public PaginatedResult<List<ViewTableLocations>> getJson(PaginatedPolygonRequest request)
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<? extends Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<? extends Record> from = select.from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()
															 .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
															 .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `view_table_locations`.`location_longitude`, ' ', `view_table_locations`.`location_latitude`, ')')))", buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, filters);

			Logger.getLogger("").log(Level.INFO, from.getSQL());

			List<ViewTableLocations> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableLocations.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
