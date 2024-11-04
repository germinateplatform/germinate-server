package jhi.germinate.server.resource.locations;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

@Path("location/polygon")
@Secured
@PermitAll
public class LocationPolygonTableResource extends BaseResource
{
	@POST
	@Path("/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableLocations>> postLocationPolygonTable(PaginatedPolygonRequest request)
		throws IOException, SQLException
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<? extends Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<? extends Record> from = select.from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()
															 .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
															 .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `view_table_locations`.`location_longitude`, ' ', `view_table_locations`.`location_latitude`, ')')))", buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			where(from, filters);

			List<ViewTableLocations> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableLocations.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/table/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postLocationPolygonTableIds(PaginatedPolygonRequest request)
		throws IOException, SQLException
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_ID)
														   .from(VIEW_TABLE_LOCATIONS);

			from.where(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull()
															 .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
															 .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `view_table_locations`.`location_longitude`, ' ', `view_table_locations`.`location_latitude`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}


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
}
