package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Germinatebase;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmResource extends PaginatedServerResource
{
	@Get("json")
	public PaginatedResult<List<Germinatebase>> getJson()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(GERMINATEBASE);

			List<Germinatebase> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Germinatebase.class);

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
