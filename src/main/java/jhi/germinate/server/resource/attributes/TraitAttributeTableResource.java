package jhi.germinate.server.resource.attributes;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraitAttributes;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableTraitAttributes.*;

@Path("trait/attribute")
@Secured
@PermitAll
public class TraitAttributeTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableTraitAttributes>> postTraitAttributeTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_TRAIT_ATTRIBUTES);

			// Filter here!
			where(from, filters);

			List<ViewTableTraitAttributes> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableTraitAttributes.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
