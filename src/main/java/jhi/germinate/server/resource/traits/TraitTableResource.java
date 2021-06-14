package jhi.germinate.server.resource.traits;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.*;

@Path("trait/table")
@Secured
@PermitAll
public class TraitTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableTraits>> postTraitTable(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_TRAITS);

			// Filter here!
			filter(from, filters, true);

			List<ViewTableTraits> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableTraits.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	public static List<ViewTableTraits> getForDataset(int datasetId)
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.select()
						  .from(VIEW_TABLE_TRAITS)
						  .where(DSL.condition("JSON_CONTAINS(" + VIEW_TABLE_TRAITS.DATASET_IDS.getName() + ", '" + datasetId + "')"))
						  .fetchInto(ViewTableTraits.class);
		}
	}
}
