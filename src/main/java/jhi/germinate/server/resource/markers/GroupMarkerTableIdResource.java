package jhi.germinate.server.resource.markers;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
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
public class GroupMarkerTableIdResource extends PaginatedServerResource
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

	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_MARKERS.MARKER_ID)
														   .from(VIEW_TABLE_MARKERS)
														   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_MARKERS.MARKER_ID))
														   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(2));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch(VIEW_TABLE_MARKERS.MARKER_ID);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
