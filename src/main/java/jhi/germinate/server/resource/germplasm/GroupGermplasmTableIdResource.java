package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.groups.GroupResource;
import org.jooq.*;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;

/**
 * @author Sebastian Raubach
 */
public class GroupGermplasmTableIdResource extends GermplasmBaseResource
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

			SelectJoinStep<Record1<Integer>> from = getGermplasmIdQuery(context)
				.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
				.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(3));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, adjustFilter(filters));

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
