package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableMarkers.*;

@Path("group/{groupId}/marker")
@Secured
@PermitAll
public class GroupMarkerTable extends BaseResource
{
	@PathParam("groupId")
	private Integer groupId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupMarkers>> postGroupMarkerTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_MARKERS.fields())
													 .select(GROUPS.ID.as("group_id"));

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MARKERS)
												.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_MARKERS.MARKER_ID))
												.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(2));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, filters);

			List<ViewTableGroupMarkers> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupMarkers.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int patchGroupMarkerTable(GroupModificationRequest modification)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		try
		{
			return GroupResource.patchGroupMembers(groupId, userDetails, modification);
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return 0;
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupMarkerTableIds(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_MARKERS.MARKER_ID)
														   .from(VIEW_TABLE_MARKERS)
														   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_MARKERS.MARKER_ID))
														   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(2));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch(VIEW_TABLE_MARKERS.MARKER_ID);

			return new PaginatedResult<>(result, result.size());
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response getJson(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_MARKERS.fields())
													 .select(GROUPS.NAME.as("group_name"))
													 .select(GROUPS.ID.as("group_id"));

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MARKERS)
												.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_MARKERS.MARKER_ID))
												.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(2));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, filters);

			Result<Record> result = setPaginationAndOrderBy(from)
				.fetch();

			return ResourceUtils.exportToZip(result, resp, "marker-group-table-");
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}
}
