package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;

/**
 * @author Sebastian Raubach
 */
public class GroupGermplasmTableResource extends GermplasmBaseResource
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

	@Patch("json")
	public int patchJson(GroupModificationRequest modification)
	{
		return GroupResource.patchGroupMembers(groupId, getRequest(), getResponse(), modification);
	}

	@Post("json")
	public PaginatedResult<List<ViewTableGroupGermplasm>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			SelectOnConditionStep<?> from = getGermplasmQuery(context, GROUPS.ID.as("group_id"))
				.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
				.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(3));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, adjustFilter(filters));

			List<ViewTableGroupGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupGermplasm.class);

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
