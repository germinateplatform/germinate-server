package jhi.germinate.server.resource.comment;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableComments;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.ViewTableComments.*;

/**
 * @author Sebastian Raubach
 */
public class CommentTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableComments>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_COMMENTS);

			// Filter here!
			filter(from, filters);

			List<ViewTableComments> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableComments.class);

			result.forEach(c -> {
				ViewUserDetails user = GatekeeperClient.getUser(c.getUserId());

				if (user != null)
					c.setUserName(user.getFullName());
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
