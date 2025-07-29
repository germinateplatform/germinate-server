package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;

@Path("group/{groupId}/germplasm")
@Secured
@PermitAll
public class GroupGermplasmTableResource extends GermplasmBaseResource
{
	@PathParam("groupId")
	private Integer groupId;

	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupGermplasm>> postGroupGermplasmTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, null, true);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
			Field<Integer> fieldGroupTypeId = DSL.field("grouptype_id", Integer.class);
			List<Join<Integer>> joins = new ArrayList<>();
			joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
			joins.add(new Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"));

			from.having(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.having(fieldGroupId.eq(groupId));

			// Filter here!
			having(from, filters);

			List<ViewTableGroupGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupGermplasm.class);

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
	public int patchGermplasmGroup(GroupModificationRequest modification)
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
			return -1;
		}
	}

	@POST
	@Path("/ids")
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupGermplasmTableIds(PaginatedRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, null, true);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
			Field<Integer> fieldGroupTypeId = DSL.field("grouptype_id", Integer.class);
			List<Join<Integer>> joins = new ArrayList<>();
			joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
			joins.add(new Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));
			SelectJoinStep<?> from = getGermplasmIdQueryWrapped(context, datasetIds, joins, fieldGroupId, fieldGroupTypeId);

			from.where(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.where(fieldGroupId.eq(groupId));

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

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
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postGroupGermplasmTableExport(ExportRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, null, true);

		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			GroupResource.checkGroupVisibility(context, userDetails, groupId);

			Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
			Field<Integer> fieldGroupTypeId = DSL.field("grouptype_id", Integer.class);
			List<Join<Integer>> joins = new ArrayList<>();
			joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
			joins.add(new Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"));

			from.having(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.having(fieldGroupId.eq(groupId));

			// Filter here!
			having(from, filters);

			return ResourceUtils.exportToZip(from.fetch(), resp, "germplasm-group-table-");
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}
}
