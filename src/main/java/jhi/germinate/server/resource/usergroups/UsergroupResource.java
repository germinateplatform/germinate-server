package jhi.germinate.server.resource.usergroups;

import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.UserGroupModificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Usergroups;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Usergroupmembers.USERGROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Usergroups.USERGROUPS;

@Path("usergroup")
@Secured({UserType.ADMIN})
public class UsergroupResource extends ContextResource
{
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer putUsergroup(Usergroups group)
			throws SQLException
	{
		if (StringUtils.isEmpty(group.getName()) || group.getId() != null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			group.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			group.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			UsergroupsRecord record = context.newRecord(USERGROUPS, group);
			record.store();
			return record.getId();
		}
	}

	@PATCH
	@Path("/{usergroupId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchJson(Usergroups group, @PathParam("usergroupId") Integer usergroupId)
			throws SQLException
	{
		if (group == null || usergroupId == null || !Objects.equals(group.getId(), usergroupId))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			UsergroupsRecord dbGroup = context.selectFrom(USERGROUPS)
			                                  .where(USERGROUPS.ID.eq(usergroupId))
			                                  .fetchAnyInto(UsergroupsRecord.class);

			if (dbGroup == null)
				throw new NotFoundException();

			// Only update the name if it's not empty
			if (!StringUtils.isEmpty(group.getName()))
				dbGroup.setName(group.getName());
			// Update the description
			dbGroup.setDescription(group.getDescription());
			return dbGroup.store(USERGROUPS.NAME, USERGROUPS.DESCRIPTION) == 1;
		}
	}

	@DELETE
	@Path("/{usergroupId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteUsergroup(@PathParam("usergroupId") Integer usergroupId)
			throws SQLException
	{
		if (usergroupId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			UsergroupsRecord dbGroup = context.selectFrom(USERGROUPS)
			                                  .where(USERGROUPS.ID.eq(usergroupId))
			                                  .fetchAnyInto(UsergroupsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbGroup == null)
				throw new NotFoundException();
			else
			{
				int res = dbGroup.delete();

				AuthorizationFilter.refreshUserDatasetInfo(true);

				return res == 1;
			}
		}
	}

	@PATCH
	@Path("/{usergroupId}/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchUser(@PathParam("usergroupId") Integer usergroupId, UserGroupModificationRequest request)
			throws SQLException
	{
		if (request == null || usergroupId == null || !usergroupId.equals(request.getUserGroupId()) || request.getAddOperation() == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			int res;
			if (request.getAddOperation())
			{
				List<Integer> existingIds = context.selectDistinct(USERGROUPMEMBERS.USER_ID).from(USERGROUPMEMBERS).where(USERGROUPMEMBERS.USERGROUP_ID.eq(request.getUserGroupId())).fetchInto(Integer.class);
				List<Integer> toAdd = new ArrayList<>(Arrays.asList(request.getUserIds()));

				toAdd.removeAll(existingIds);

				InsertValuesStep2<UsergroupmembersRecord, Integer, Integer> step = context.insertInto(USERGROUPMEMBERS, USERGROUPMEMBERS.USER_ID, USERGROUPMEMBERS.USERGROUP_ID);

				toAdd.forEach(id -> step.values(id, request.getUserGroupId()));

				res = step.execute();
			}
			else
			{
				res = context.deleteFrom(USERGROUPMEMBERS)
				             .where(USERGROUPMEMBERS.USERGROUP_ID.eq(request.getUserGroupId()))
				             .and(USERGROUPMEMBERS.USER_ID.in(request.getUserIds()))
				             .execute();
			}

			AuthorizationFilter.refreshUserDatasetInfo(true);

			return res > 0;
		}
	}

	@GET
	@Path("/{usergroupId}/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewUserDetails> getUserForGroupId(@PathParam("usergroupId") Integer usergroupId)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			List<ViewUserDetails> result = context.select(
														  USERGROUPMEMBERS.USER_ID.as("id"),
														  DSL.val("", String.class).as("username"),
														  DSL.val("", String.class).as("full_name"),
														  DSL.val("", String.class).as("email_address"),
														  DSL.val("", String.class).as("name")
												  )
			                                      .from(USERGROUPMEMBERS)
			                                      .where(USERGROUPMEMBERS.USERGROUP_ID.eq(usergroupId))
			                                      .fetchInto(ViewUserDetails.class);

			return result.stream()
			             .map(r -> GatekeeperClient.getUser(r.getId()))
			             .filter(Objects::nonNull)
			             .collect(Collectors.toList());
		}
	}
}
