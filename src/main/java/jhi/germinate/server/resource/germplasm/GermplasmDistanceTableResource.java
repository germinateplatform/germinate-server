package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Locations.*;

@Path("germplasm/distance")
@Secured
@PermitAll
public class GermplasmDistanceTableResource extends GermplasmBaseResource
{
	@POST
	@Path("/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<GermplasmDistance>> postGermplasmDistanceTable(PaginatedLocationRequest request)
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
			Field<BigDecimal> dLat = DSL.rad(LOCATIONS.LATITUDE.minus(request.getLatitude()));
			Field<BigDecimal> dLon = DSL.rad(LOCATIONS.LONGITUDE.minus(request.getLongitude()));

			Field<BigDecimal> a = DSL.power(DSL.sin(dLon.div(2)), 2)
									 .times(DSL.cos(DSL.rad(request.getLatitude())))
									 .times(DSL.cos(DSL.rad(LOCATIONS.LATITUDE)))
									 .plus(DSL.power(DSL.sin(dLat.div(2)), 2));

			Field<BigDecimal> c = DSL.asin(DSL.sqrt(a)).times(2);

			SelectConditionStep<?> from = getGermplasmQueryWrapped(context, null, DSL.cast(c.times(6372.8), Double.class).as("distance"))
				.where(DSL.field(LONGITUDE).isNotNull())
				.and(DSL.field(LATITUDE).isNotNull());

			// Filter here!
			filter(from, filters);

			List<GermplasmDistance> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(GermplasmDistance.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/table/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGermplasmDistanceTableIds(PaginatedLocationRequest request)
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
			SelectConditionStep<?> from = getGermplasmIdQueryWrapped(context, null)
				.where(DSL.field(LONGITUDE).isNotNull())
				.and(DSL.field(LATITUDE).isNotNull());

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
