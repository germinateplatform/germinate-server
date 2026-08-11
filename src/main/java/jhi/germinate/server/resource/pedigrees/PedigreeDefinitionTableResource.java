package jhi.germinate.server.resource.pedigrees;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePedigreedefinitions;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jooq.Record;

import java.sql.*;
import java.util.*;

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
		List<Integer> datasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "pedigree", true);
		if (CollectionUtils.isEmpty(datasets))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_PEDIGREEDEFINITIONS)
													 .where(VIEW_TABLE_PEDIGREEDEFINITIONS.DATASET_ID.in(datasets));

			// Filter here!
			where(from, filters);

			List<ViewTablePedigreedefinitions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTablePedigreedefinitions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
