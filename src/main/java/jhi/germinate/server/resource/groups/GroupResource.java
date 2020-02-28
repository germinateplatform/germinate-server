package jhi.germinate.server.resource.groups;

import org.jooq.*;
import org.jooq.impl.TableImpl;
import org.restlet.*;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.GroupModificationRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.Groups;
import jhi.germinate.server.database.tables.records.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Groupmembers.*;
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

	public static int patchGroupMembers(Integer groupId, Request req, Response resp, GroupModificationRequest modification)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(req, resp);

		if (groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			Groups group = context.selectFrom(GROUPS)
								  .where(GROUPS.ID.eq(groupId))
								  .and(GROUPS.CREATED_BY.eq(userDetails.getId()))
								  .fetchOneInto(Groups.class);

			if (group == null)
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
			if (modification == null || modification.getIds() == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

			if (modification.isAddition())
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
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	public static void checkGroupVisibility(DSLContext context, CustomVerifier.UserDetails userDetails, Integer groupId) {
		if (groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		Groups group = context.selectFrom(GROUPS)
							  .where(GROUPS.ID.eq(groupId))
							  .and(GROUPS.VISIBILITY.eq(true)
													.or(GROUPS.CREATED_BY.eq(userDetails.getId())))
							  .fetchOneInto(Groups.class);

		if (group == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	}

	public static SelectJoinStep<Record> prepareQuery(Request request, Response response, DSLContext context, Integer groupId, TableImpl table, Field<Integer> field, PaginatedServerResource callee, boolean isIdQuery)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(request, response);

		if (groupId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		Groups group = context.selectFrom(GROUPS)
							  .where(GROUPS.ID.eq(groupId))
							  .and(GROUPS.VISIBILITY.eq(true)
													.or(GROUPS.CREATED_BY.eq(userDetails.getId())))
							  .fetchOneInto(Groups.class);

		if (group == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		SelectSelectStep<Record> select;

		if (isIdQuery)
			select = context.selectDistinct();
		else
			select = context.select();

		if (!isIdQuery && callee.getPreviousCount() == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<Record> from = select.from(table);

		from.where(field.eq(group.getId()));

		return from;
	}

	@Delete("json")
	@MinUserType(UserType.AUTH_USER)
	public boolean deleteJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

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
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

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

	@Put("json")
	@MinUserType(UserType.AUTH_USER)
	public Integer putJson(Groups group)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (group.getCreatedBy() == null || !Objects.equals(group.getCreatedBy(), userDetails.getId()))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		if (StringUtils.isEmpty(group.getName()) || group.getGrouptypeId() == null || group.getId() != null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			group.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			group.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			GroupsRecord record = context.newRecord(GROUPS, group);
			record.store();
			return record.getId();
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
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
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
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
