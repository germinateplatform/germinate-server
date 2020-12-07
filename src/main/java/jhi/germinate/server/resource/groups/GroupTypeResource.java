package jhi.germinate.server.resource.groups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Grouptypes;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Get;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Grouptypes.*;

/**
 * @author Sebastian Raubach
 */
public class GroupTypeResource extends PaginatedServerResource
{
	@Get("json")
	public PaginatedResult<List<Grouptypes>> getJson()
	{
		try (DSLContext context = Database.getContext())
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
	}
}
