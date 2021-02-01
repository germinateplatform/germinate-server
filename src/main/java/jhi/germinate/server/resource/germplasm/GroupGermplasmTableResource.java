package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.groups.GroupResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.*;

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
		try (DSLContext context = Database.getContext())
		{
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
			Field<Integer> fieldGroupTypeId = DSL.field("grouptype_id", Integer.class);
			List<Join<Integer>> joins = new ArrayList<>();
			joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
			joins.add(new Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, joins, GROUPS.ID.as("group_id"), GROUPS.GROUPTYPE_ID.as("grouptype_id"));

			from.where(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.where(fieldGroupId.eq(groupId));

			// Filter here!
			filter(from, filters);

			List<ViewTableGroupGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
