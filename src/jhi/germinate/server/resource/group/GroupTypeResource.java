package jhi.germinate.server.resource.group;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.Grouptypes;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.Grouptypes.*;

/**
 * @author Sebastian Raubach
 */
public class GroupTypeResource extends PaginatedServerResource
{
	@Get("json")
	public PaginatedResult<List<Grouptypes>> getJson()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(GROUPTYPES);

			List<Grouptypes> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Grouptypes.class);

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
