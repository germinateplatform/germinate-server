package jhi.germinate.server.resource.institutions;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableInstitutionDatasets;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableInstitutionDatasets.VIEW_TABLE_INSTITUTION_DATASETS;

@Path("institution/dataset/table")
@Secured
@PermitAll
public class InstitutionDatasetTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postInstitutionTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_INSTITUTION_DATASETS);

			// Filter here!
			where(from, filters);

			List<ViewTableInstitutionDatasets> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableInstitutionDatasets.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return Response.ok(new PaginatedResult<>(result, count)).build();
		}
	}
}
