package jhi.germinate.server.resource.collaborator;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableCollaborators;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableCollaborators.VIEW_TABLE_COLLABORATORS;

@Path("collaborator/table")
@Secured
@PermitAll
public class CollaboratorTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postCollaboratorTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_COLLABORATORS);

			// Filter here!
			where(from, filters, true);

			List<ViewTableCollaborators> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableCollaborators.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return Response.ok(new PaginatedResult<>(result, count)).build();
		}
	}
}
