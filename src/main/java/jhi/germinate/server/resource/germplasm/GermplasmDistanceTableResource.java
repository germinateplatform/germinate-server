package jhi.germinate.server.resource.germplasm;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.util.*;
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
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, null, true);

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

			SelectHavingConditionStep<?> from = getGermplasmQueryWrapped(context, datasetIds, null, DSL.cast(c.times(6372.8), Double.class).as("distance"))
				.having(DSL.field(LONGITUDE).isNotNull()
				.and(DSL.field(LATITUDE).isNotNull()));

			// Filter here!
			having(from, filters);

			List<GermplasmDistance> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(GermplasmDistance.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/table/ids")
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, null, true);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectConditionStep<?> from = getGermplasmIdQueryWrapped(context, datasetIds, null)
				.where(DSL.field(LONGITUDE).isNotNull())
				.and(DSL.field(LATITUDE).isNotNull());

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
