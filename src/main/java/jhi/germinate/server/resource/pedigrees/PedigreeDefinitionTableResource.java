package jhi.germinate.server.resource.pedigrees;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePedigreedefinitions;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTablePedigreedefinitions.*;

@Path("pedigreedefinition")
@Secured
@PermitAll
public class PedigreeDefinitionTableResource extends ExportResource
{
	@Path("/table")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTablePedigreedefinitions>> postPedigreeTable(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_PEDIGREEDEFINITIONS);

			// Filter here!
			filter(from, filters);

			List<ViewTablePedigreedefinitions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTablePedigreedefinitions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@Path("/table/export")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response getJson(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

		return export(VIEW_TABLE_PEDIGREEDEFINITIONS, "pedigree-definitions-table-", null);
	}
}
