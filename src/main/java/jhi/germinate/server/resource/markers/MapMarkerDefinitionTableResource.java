package jhi.germinate.server.resource.markers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.pojo.ViewTableMapdefinitions;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.List;

@Path("map/mapdefinition/table")
@Secured
@PermitAll
public class MapMarkerDefinitionTableResource extends MapdefinitionBaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableMapdefinitions>> postMapMarkerDefinitionTable(PaginatedRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectHavingConditionStep<?> from = getMapdefinitionQuery(context)
					.having(DSL.field(VISIBILITY, Boolean.class).eq(true)
							   .or(DSL.field(USER_ID, Integer.class).eq(userDetails.getId())));

			// Filter here!
			having(from, filters);

			List<ViewTableMapdefinitions> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableMapdefinitions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postMapMarkerDefinitionTableIds(PaginatedRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectHavingConditionStep<?> from = getMapDefinitionIdQuery(context)
					.having(DSL.field(VISIBILITY, Boolean.class).eq(true)
							   .or(DSL.field(USER_ID, Integer.class).eq(userDetails.getId())));

			// Filter here!
			having(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postMapMarkerDefinitionTableExport(PaginatedRequest request)
			throws SQLException, IOException
	{
		processRequest(request);

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectHavingConditionStep<?> from = getMapdefinitionQuery(context)
					.having(DSL.field(VISIBILITY, Boolean.class).eq(true)
							   .or(DSL.field(USER_ID, Integer.class).eq(userDetails.getId())));;

			// Filter here!
			having(from, filters);

			return ResourceUtils.exportToZip(from.fetch(), resp, "map-definition-table-");
		}
	}
}
