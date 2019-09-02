package jhi.germinate.server.resource.group;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.Groups;
import jhi.germinate.server.database.tables.records.GroupsRecord;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.Groups.*;

/**
 * @author Sebastian Raubach
 */
public class GroupResource extends PaginatedServerResource
{
	private Integer groupId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.groupId = Integer.parseInt(getRequestAttributes().get("groupId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Delete("json")
	@MinUserType(UserType.AUTH_USER)
	public boolean deleteJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		if (groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			GroupsRecord dbGroup = context.selectFrom(GROUPS)
										  .where(GROUPS.ID.eq(groupId))
										  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
										  .fetchOneInto(GroupsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbGroup == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			else
				return dbGroup.delete() == 1;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Patch("json")
	@MinUserType(UserType.AUTH_USER)
	public boolean patchJson(Groups group)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		if (group == null || groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id or payload");
		if (!Objects.equals(group.getId(), groupId))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Id mismatch");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			GroupsRecord dbGroup = context.selectFrom(GROUPS)
										  .where(GROUPS.ID.eq(groupId))
										  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
										  .fetchOneInto(GroupsRecord.class);

			if (dbGroup == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

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
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Get("json")
	public PaginatedResult<List<Groups>> getJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(GROUPS);

			from.where(GROUPS.VISIBILITY.eq((byte) 1)
										.or(GROUPS.CREATED_BY.eq(userDetails.getId())));

			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			List<Groups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Groups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
