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
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;

@Path("group/{groupId}/marker")
@Secured
@PermitAll
public class GroupMarkerTableResource extends MarkerBaseResource
{
	@PathParam("groupId")
	private Integer groupId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupMarkers>> postGroupMarkerTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			List<GermplasmBaseResource.Join<Integer>> joins = new ArrayList<>();
			joins.add(new GermplasmBaseResource.Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, MARKERS.ID));
			joins.add(new GermplasmBaseResource.Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));

			SelectHavingConditionStep<?> from = getMarkerQuery(context, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"))
					.having(DSL.field("grouptype_id", Integer.class).eq(2));

			if (groupId != null)
				from.and(DSL.field("group_id", Integer.class).eq(groupId));

			// Filter here!
			having(from, filters);

			List<ViewTableGroupMarkers> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableGroupMarkers.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int patchGroupMarkerTable(GroupModificationRequest modification)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		return GroupResource.patchGroupMembers(groupId, userDetails, modification);
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupMarkerTableIds(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			List<GermplasmBaseResource.Join<Integer>> joins = new ArrayList<>();
			joins.add(new GermplasmBaseResource.Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, MARKERS.ID));
			joins.add(new GermplasmBaseResource.Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));

			SelectHavingConditionStep<?> from = getMarkerIdQuery(context, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"))
					.having(DSL.field("grouptype_id", Integer.class).eq(2));

			if (groupId != null)
				from.and(DSL.field("group_id", Integer.class).eq(groupId));

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
	public java.io.File postGroupMarkerTableExport(ExportRequest request, @Context HttpServletResponse response)
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

			List<GermplasmBaseResource.Join<Integer>> joins = new ArrayList<>();
			joins.add(new GermplasmBaseResource.Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, MARKERS.ID));
			joins.add(new GermplasmBaseResource.Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));

			SelectHavingConditionStep<?> from = getMarkerQuery(context, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"), GROUPS.NAME.as("group_name"))
					.having(DSL.field("grouptype_id", Integer.class).eq(2));

			if (groupId != null)
				from.and(DSL.field("group_id", Integer.class).eq(groupId));

			// Filter here!
			having(from, filters);

			java.io.File result = ResourceUtils.exportToZip(from.fetch(), "marker-group-table-");

			return toFileResult(result, "application/zip", response);
		}
	}
}
