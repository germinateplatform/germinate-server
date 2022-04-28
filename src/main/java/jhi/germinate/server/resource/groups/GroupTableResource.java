package jhi.germinate.server.resource.groups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableGroups.*;

@Path("group/table")
@Secured
@PermitAll
public class GroupTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroups>> getJson(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
	}
}
