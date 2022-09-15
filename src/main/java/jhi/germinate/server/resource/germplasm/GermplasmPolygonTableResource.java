package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.locations.LocationPolygonTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Locations.*;

@Path("germplasm/polygon")
@Secured
@PermitAll
public class GermplasmPolygonTableResource extends GermplasmBaseResource
{
	@POST
	@Path("/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGermplasm>> postGermplasmPolygonTable(PaginatedPolygonRequest request)
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
			SelectConditionStep<?> from = getGermplasmQueryWrapped(context, null)
				.where(DSL.field(LATITUDE).isNotNull()
						  .and(DSL.field(LONGITUDE).isNotNull())
						  .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `" + LOCATIONS.LONGITUDE.getName() + "`, ' ', `" + LOCATIONS.LATITUDE.getName() + "`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, filters);

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/table/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGermplasmPolgonTableIds(PaginatedPolygonRequest request)
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
			SelectConditionStep<Record1<Integer>> from = getGermplasmIdQueryWrapped(context, null)
				.where(DSL.field(LATITUDE).isNotNull()
						  .and(DSL.field(LONGITUDE).isNotNull())
						  .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `" + LOCATIONS.LONGITUDE.getName() + "`, ' ', `" + LOCATIONS.LATITUDE.getName() + "`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
