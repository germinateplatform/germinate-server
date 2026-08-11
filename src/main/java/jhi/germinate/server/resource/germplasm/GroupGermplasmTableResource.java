package jhi.germinate.server.resource.germplasm;

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
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;

@Path("group/{groupId}/germplasm")
@Secured
@PermitAll
public class GroupGermplasmTableResource extends GermplasmBaseResource
{
	@PathParam("groupId")
	private Integer groupId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroupGermplasm>> postGroupGermplasmTable(PaginatedRequest request)
			throws StatusException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, true);

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
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, false, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"));

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
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int patchGermplasmGroup(GroupModificationRequest modification)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		return GroupResource.patchGroupMembers(groupId, userDetails, modification);
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGroupGermplasmTableIds(PaginatedRequest request)
			throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, true);

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
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public File postGroupGermplasmTableExport(ExportRequest request, @Context HttpServletResponse response)
			throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, true);

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
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, false, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"));

			from.having(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.having(fieldGroupId.eq(groupId));

			// Filter here!
			having(from, filters);

			File result = ResourceUtils.exportToZip(from.fetch(), "germplasm-group-table-");

			return toFileResult(result, "application/zip", response);
		}
	}
}
