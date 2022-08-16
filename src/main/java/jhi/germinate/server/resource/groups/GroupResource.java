package jhi.germinate.server.resource.groups;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.GroupModificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PublicationdataReferenceType;
import jhi.germinate.server.database.codegen.tables.pojos.Groups;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Publicationdata.*;

@Path("group")
@Secured
public class GroupResource extends BaseResource
{
	public static List<Integer> getGroupIdsForUser(AuthenticationFilter.UserDetails userDetails, Integer foreignId)
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectConditionStep<Record1<Integer>> where = context.select(GROUPS.ID)
																 .from(GROUPS)
																 .where(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId())));

			if (foreignId != null)
				where.andExists(DSL.selectOne().from(GROUPMEMBERS).where(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID).and(GROUPMEMBERS.FOREIGN_ID.eq(foreignId))));

			return where.fetchInto(Integer.class);
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public PaginatedResult<List<Groups>> getGroups()
		throws SQLException
	{
		return getGroups(null);
	}

	@GET
	@Path("/{groupId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public PaginatedResult<List<Groups>> getGroups(@PathParam("groupId") Integer groupId)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(GROUPS);

			from.where(GROUPS.VISIBILITY.eq(true)
										.or(GROUPS.CREATED_BY.eq(userDetails.getId())));

			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			List<Groups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Groups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.AUTH_USER})
	public Integer putGroup(Groups group)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (group.getCreatedBy() == null || !Objects.equals(group.getCreatedBy(), userDetails.getId()))
		{
			resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
			return null;
		}
		if (StringUtils.isEmpty(group.getName()) || group.getGrouptypeId() == null || group.getId() != null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			group.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			group.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			GroupsRecord record = context.newRecord(GROUPS, group);
			record.store();
			return record.getId();
		}
	}

	@PATCH
	@Path("/{groupId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.AUTH_USER})
	public boolean patchGroup(Groups group, @PathParam("groupId") Integer groupId)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (group == null || groupId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Missing id or payload");
			return false;
		}
		if (!Objects.equals(group.getId(), groupId))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Id mismatch");
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			GroupsRecord dbGroup = context.selectFrom(GROUPS)
										  .where(GROUPS.ID.eq(groupId))
										  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
										  .fetchAnyInto(GroupsRecord.class);

			if (dbGroup == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			// Only update the name if it's not empty
			if (!StringUtils.isEmpty(group.getName()))
				dbGroup.setName(group.getName());
			// Only update visibility if it's not null
			if (group.getVisibility() != null)
				dbGroup.setVisibility(group.getVisibility());
			// Update the description
			dbGroup.setDescription(group.getDescription());
			return dbGroup.store() == 1;
		}
	}

	@DELETE
	@Path("/{groupId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.AUTH_USER})
	public boolean deleteGroup(@PathParam("groupId") Integer groupId)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (groupId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Missing id");
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			GroupsRecord dbGroup = context.selectFrom(GROUPS)
										  .where(GROUPS.ID.eq(groupId))
										  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
										  .fetchAnyInto(GroupsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbGroup == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}
			else
			{
				// Delete the publication reference
				context.deleteFrom(PUBLICATIONDATA)
					   .where(PUBLICATIONDATA.REFERENCE_TYPE.eq(PublicationdataReferenceType.group))
					   .and(PUBLICATIONDATA.FOREIGN_ID.eq(dbGroup.getId()))
					   .execute();

				return dbGroup.delete() == 1;
			}
		}
	}

	public static void checkGroupVisibility(DSLContext context, AuthenticationFilter.UserDetails userDetails, Integer groupId)
		throws GerminateException
	{
		if (groupId == null)
			throw new GerminateException(Response.Status.BAD_REQUEST, "Missing id");

		Groups group = context.selectFrom(GROUPS)
							  .where(GROUPS.ID.eq(groupId))
							  .and(GROUPS.VISIBILITY.eq(true)
													.or(GROUPS.CREATED_BY.eq(userDetails.getId())))
							  .fetchAnyInto(Groups.class);

		if (group == null)
			throw new GerminateException(Response.Status.NOT_FOUND);
	}

	public static int patchGroupMembers(Integer groupId, AuthenticationFilter.UserDetails userDetails, GroupModificationRequest modification)
		throws GerminateException, SQLException
	{
		if (groupId == null)
			throw new GerminateException(Response.Status.BAD_REQUEST, "Missing id");

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Groups group = context.selectFrom(GROUPS)
								  .where(GROUPS.ID.eq(groupId))
								  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
								  .fetchAnyInto(Groups.class);

			if (group == null)
				throw new GerminateException(Response.Status.FORBIDDEN);
			if (modification == null || modification.getIds() == null)
				throw new GerminateException(Response.Status.BAD_REQUEST);

			if (modification.getAddition())
			{
				// We need to make sure not to introduce duplicates
				List<Integer> newIds = new ArrayList<>(Arrays.asList(modification.getIds()));
				List<Integer> existingIds = context.select(GROUPMEMBERS.FOREIGN_ID)
												   .from(GROUPMEMBERS)
												   .where(GROUPMEMBERS.GROUP_ID.eq(group.getId()))
												   .fetchInto(Integer.class);

				// Remove all existing ids
				newIds.removeAll(existingIds);

				// Then insert the remaining ones
				InsertValuesStep2<GroupmembersRecord, Integer, Integer> step = context.insertInto(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GROUPMEMBERS.GROUP_ID);
				newIds.forEach(i -> step.values(i, group.getId()));
				return step.execute();
			}
			else
			{
				// Simply delete the ids
				return context.deleteFrom(GROUPMEMBERS)
							  .where(GROUPMEMBERS.GROUP_ID.eq(group.getId()))
							  .and(GROUPMEMBERS.FOREIGN_ID.in(modification.getIds()))
							  .execute();
			}
		}
	}
}
