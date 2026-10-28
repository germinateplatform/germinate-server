package jhi.germinate.server.resource.markers;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapdefinitions;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.conf.ParamType;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapdefinitions.VIEW_TABLE_MAPDEFINITIONS;

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

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_MAPDEFINITIONS)
													 .where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true).or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			where(from, filters);

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
			SelectConditionStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_MAPDEFINITIONS.MARKER_ID)
																.from(VIEW_TABLE_MAPDEFINITIONS)
																.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true).or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			where(from, filters);

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
	public File postMapMarkerDefinitionTableExport(ExportRequest request, @Context HttpServletResponse response)
			throws SQLException
	{
		processRequest(request);

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true).or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId()))};

		File result = export(VIEW_TABLE_MAPDEFINITIONS, "mapdefinition-table-", null);

		return toFileResult(result, "application/zip", response);
	}
}
