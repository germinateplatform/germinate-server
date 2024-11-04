package jhi.germinate.server.resource.feedback;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Userfeedback;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Userfeedback.*;

@Path("feedback/table")
@Secured(UserType.ADMIN)
@PermitAll
public class FeedbackTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Userfeedback>> getJson(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(USERFEEDBACK);

			// Filter here!
			where(from, filters);

			List<Userfeedback> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Userfeedback.class);

			// Remove the image byte[] here, we don't want to send it in the json response. Call the /feedback/{id}/img endpoint to get the image.
			result.forEach(r -> r.setImage(null));

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
