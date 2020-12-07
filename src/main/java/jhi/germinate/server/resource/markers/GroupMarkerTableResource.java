package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.groups.GroupResource;
import org.jooq.*;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableMarkers.*;

/**
 * @author Sebastian Raubach
 */
public class GroupMarkerTableResource extends PaginatedServerResource
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
	public PaginatedResult<List<ViewTableGroupMarkers>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_MARKERS.fields())
													 .select(GROUPS.ID.as("group_id"));

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MARKERS)
												.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_MARKERS.MARKER_ID))
												.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(2));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, filters);

			List<ViewTableGroupMarkers> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupMarkers.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
