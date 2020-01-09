package jhi.germinate.server.resource.users;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.UserGroupModificationRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.records.UsergroupmembersRecord;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.BaseServerResource;

import static jhi.germinate.server.database.tables.Usergroupmembers.*;

/**
 * @author Sebastian Raubach
 */
public class UserResource extends BaseServerResource
{
	private Integer groupId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.groupId = Integer.parseInt(getRequestAttributes().get("usergroupId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@MinUserType(UserType.ADMIN)
	@Patch("json")
	public boolean patchJson(UserGroupModificationRequest request)
	{
		if (request == null || this.groupId == null || this.groupId != request.getUserGroupId() || request.isAddOperation() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			if (request.isAddOperation())
			{
				List<Integer> existingIds = context.selectDistinct(USERGROUPMEMBERS.USER_ID).from(USERGROUPMEMBERS).where(USERGROUPMEMBERS.USERGROUP_ID.eq(request.getUserGroupId())).fetchInto(Integer.class);
				List<Integer> toAdd = new ArrayList<>(Arrays.asList(request.getUserIds()));

				toAdd.removeAll(existingIds);

				InsertValuesStep2<UsergroupmembersRecord, Integer, Integer> step = context.insertInto(USERGROUPMEMBERS, USERGROUPMEMBERS.USER_ID, USERGROUPMEMBERS.USERGROUP_ID);

				toAdd.forEach(id -> step.values(id, request.getUserGroupId()));

				return step.execute() > 0;
			}
			else
			{
				return context.deleteFrom(USERGROUPMEMBERS)
							  .where(USERGROUPMEMBERS.USERGROUP_ID.eq(request.getUserGroupId()))
							  .and(USERGROUPMEMBERS.USER_ID.in(request.getUserIds()))
							  .execute() > 0;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@MinUserType(UserType.ADMIN)
	@Get("json")
	public List<ViewUserDetails> getJson()
	{
		if (groupId == null)
		{
			return GatekeeperClient.getUsers();
		}
		else
		{
			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn))
			{
				List<ViewUserDetails> result = context.select(
					USERGROUPMEMBERS.USER_ID.as("id"),
					DSL.val("", String.class).as("username"),
					DSL.val("", String.class).as("full_name"),
					DSL.val("", String.class).as("email_address"),
					DSL.val("", String.class).as("name")
				)
													  .from(USERGROUPMEMBERS)
													  .where(USERGROUPMEMBERS.USERGROUP_ID.eq(groupId))
													  .fetchInto(ViewUserDetails.class);

				return result.stream()
							 .map(r -> GatekeeperClient.getUser(r.getId()))
							 .filter(Objects::nonNull)
							 .collect(Collectors.toList());
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}
		}
	}
}
