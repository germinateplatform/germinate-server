package jhi.germinate.server.resource.usergroups;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.Objects;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.Usergroups;
import jhi.germinate.server.database.tables.records.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Usergroups.*;

/**
 * @author Sebastian Raubach
 */
public class UsergroupResource extends BaseServerResource
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

	@Patch("json")
	@MinUserType(UserType.AUTH_USER)
	public boolean patchJson(Usergroups group)
	{
		if (group == null || groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id or payload");
		if (!Objects.equals(group.getId(), groupId))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Id mismatch");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			GroupsRecord dbGroup = context.selectFrom(USERGROUPS)
										  .where(USERGROUPS.ID.eq(groupId))
										  .fetchOneInto(GroupsRecord.class);

			if (dbGroup == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			// Only update the name if it's not empty
			if (!StringUtils.isEmpty(group.getName()))
				dbGroup.setName(group.getName());
			// Update the description
			dbGroup.setDescription(group.getDescription());
			return dbGroup.store() == 1;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Put("json")
	public Integer putJson(Usergroups group)
	{
		if (StringUtils.isEmpty(group.getName()) || group.getId() != null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			group.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			group.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			UsergroupsRecord record = context.newRecord(USERGROUPS, group);
			record.store();
			return record.getId();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Delete("json")
	@MinUserType(UserType.ADMIN)
	public boolean deleteJson()
	{
		if (groupId == null)
			throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			UsergroupsRecord dbGroup = context.selectFrom(USERGROUPS)
											  .where(USERGROUPS.ID.eq(groupId))
											  .fetchOneInto(UsergroupsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbGroup == null)
				throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			else
				return dbGroup.delete() == 1;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
