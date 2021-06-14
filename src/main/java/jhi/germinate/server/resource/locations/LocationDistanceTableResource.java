package jhi.germinate.server.resource.locations;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

@Path("location/distance/table")
@Secured
@PermitAll
public class LocationDistanceTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<LocationDistance>> postLocationDistanceTable(PaginatedLocationRequest request)
		throws IOException, SQLException
	{
		if (request.getLatitude() == null || request.getLongitude() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> getJson(PaginatedLocationRequest request)
		throws IOException, SQLException
	{
		if (request.getLatitude() == null || request.getLongitude() == null)
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
