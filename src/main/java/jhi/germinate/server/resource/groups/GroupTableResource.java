package jhi.germinate.server.resource.groups;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.ViewTableGroups.*;

/**
 * @author Sebastian Raubach
 */
public class GroupTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableGroups>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GROUPS);

			from.where(VIEW_TABLE_GROUPS.GROUP_VISIBILITY.eq(true)
														 .or(VIEW_TABLE_GROUPS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<ViewTableGroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroups.class);


			result.forEach(g -> {
				ViewUserDetails user = GatekeeperClient.getUser(g.getUserId());

				if (user != null)
					g.setUserName(user.getFullName());
			});

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
