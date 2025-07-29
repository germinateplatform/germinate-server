package jhi.germinate.server.resource.comment;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableComments;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jooq.Record;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableComments.*;

@Path("comment/table")
@Secured
@PermitAll
public class CommentTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableComments>> postCommentTable(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_COMMENTS);

			// Filter here!
			where(from, filters);

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
	}
}
