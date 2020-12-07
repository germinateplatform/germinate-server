package jhi.germinate.server.resource.usergroups;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.Usergroups;
import jhi.germinate.server.database.codegen.tables.records.UsergroupsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.Timestamp;
import java.util.Objects;

import static jhi.germinate.server.database.codegen.tables.Usergroups.*;

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
	@MinUserType(UserType.ADMIN)
	public boolean patchJson(Usergroups group)
	{
		if (group == null || groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id or payload");
		if (!Objects.equals(group.getId(), groupId))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Id mismatch");

		try (DSLContext context = Database.getContext())
		{
			UsergroupsRecord dbGroup = context.selectFrom(USERGROUPS)
										  .where(USERGROUPS.ID.eq(groupId))
										  .fetchAnyInto(UsergroupsRecord.class);

			if (dbGroup == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			// Only update the name if it's not empty
			if (!StringUtils.isEmpty(group.getName()))
				dbGroup.setName(group.getName());
			// Update the description
			dbGroup.setDescription(group.getDescription());
			return dbGroup.store(USERGROUPS.NAME, USERGROUPS.DESCRIPTION) == 1;
		}
	}

	@Put("json")
	@MinUserType(UserType.ADMIN)
	public Integer putJson(Usergroups group)
	{
		if (StringUtils.isEmpty(group.getName()) || group.getId() != null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			group.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			group.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			UsergroupsRecord record = context.newRecord(USERGROUPS, group);
			record.store();
			return record.getId();
		}
	}

	@Delete("json")
	@MinUserType(UserType.ADMIN)
	public boolean deleteJson()
	{
		if (groupId == null)
			throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		try (DSLContext context = Database.getContext())
		{
			UsergroupsRecord dbGroup = context.selectFrom(USERGROUPS)
											  .where(USERGROUPS.ID.eq(groupId))
											  .fetchAnyInto(UsergroupsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbGroup == null)
				throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			else
				return dbGroup.delete() == 1;
		}
	}
}
