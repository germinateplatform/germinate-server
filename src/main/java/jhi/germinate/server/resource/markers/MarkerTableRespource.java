package jhi.germinate.server.resource.markers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.ViewTableMarkers;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

@Path("marker/table")
@Secured
@PermitAll
public class MarkerTableRespource extends MarkerBaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableMarkers>> postMarkerTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectJoinStep<?> from = getMarkerQuery(context, null);

			// Filter here!
			having(from, filters);

			List<ViewTableMarkers> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableMarkers.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postMarkerTableIds(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from = getMarkerIdQuery(context, null);

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
	public Response postMarkerTableExport(ExportRequest request)
			throws SQLException, IOException
	{
		processRequest(request);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from = getMarkerQuery(context, null);

			// Filter here!
			having(from, filters);

			return ResourceUtils.exportToZip(from.fetch(), resp, "marker-table-");
		}
	}
}
