package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapdefinitions;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapdefinitions.*;

@Path("map/mapdefinition/table")
@Secured
@PermitAll
public class MapMarkerDefinitionTableResource extends ExportResource
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
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

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
			SelectSelectStep<Record1<Integer>> select = context.selectDistinct(VIEW_TABLE_MAPDEFINITIONS.MARKER_ID);

			SelectJoinStep<Record1<Integer>> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

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
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		processRequest(request);

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true).or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId()))};
		settings.fieldsToNull = new Field[]{VIEW_TABLE_MAPDEFINITIONS.USER_ID, VIEW_TABLE_MAPDEFINITIONS.VISIBILITY};
		return export(VIEW_TABLE_MAPDEFINITIONS, "map-definition-table-", settings);
	}
}
