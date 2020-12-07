package jhi.germinate.server.resource.usergroups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableUsergroups.*;

/**
 * @author Sebastian Raubach
 */
public class UsergroupTableIdResource extends PaginatedServerResource
{
	@MinUserType(UserType.ADMIN)
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (DSLContext context = Database.getContext())
		{
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_USERGROUPS.USER_GROUP_ID)
														   .from(VIEW_TABLE_USERGROUPS);

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
