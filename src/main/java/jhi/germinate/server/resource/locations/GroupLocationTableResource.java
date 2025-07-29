package jhi.germinate.server.resource.locations;

import jakarta.ws.rs.Path;
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
import org.jooq.Record;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupLocations>> postGroupLocationTable(PaginatedRequest request)
		throws IOException, SQLException
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
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupLocationTableIds(PaginatedRequest request)
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
	public Response getJson(ExportRequest request)
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

			return ResourceUtils.exportToZip(from.fetch(), resp, "marker-group-table-");
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}
}
