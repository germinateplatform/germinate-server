package jhi.germinate.server.resource.locations;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("group/{groupId}/location")
@Secured
@PermitAll
public class GroupLocationTableResource extends BaseResource
{
	@PathParam("groupId")
	private Integer groupId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int patchGroupLocationTable(GroupModificationRequest modification)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		return GroupResource.patchGroupMembers(groupId, userDetails, modification);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupLocations>> postGroupLocationTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_LOCATIONS.fields())
			                                         .select(GROUPS.ID.as("group_id"));

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_LOCATIONS)
			                                    .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
			                                    .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(1));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			where(from, filters);

			List<ViewTableGroupLocations> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableGroupLocations.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupLocationTableIds(PaginatedRequest request)
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

			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_ID)
			                                               .from(VIEW_TABLE_LOCATIONS)
			                                               .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
			                                               .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(1));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
					.fetch(VIEW_TABLE_LOCATIONS.LOCATION_ID);

			return new PaginatedResult<>(result, result.size());
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public java.io.File getJson(ExportRequest request, @Context HttpServletResponse response)
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

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_LOCATIONS.fields())
			                                         .select(GROUPS.NAME.as("group_name"))
			                                         .select(GROUPS.ID.as("group_id"));

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_LOCATIONS)
			                                    .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
			                                    .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(1));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			where(from, filters);

			java.io.File result = ResourceUtils.exportToZip(from.fetch(), "location-group-table-");

			return toFileResult(result, "application/zip", response);
		}
	}
}
