package jhi.germinate.server.resource.institution;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableInstitutions;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableInstitutions.*;

/**
 * @author Sebastian Raubach
 */
public class InstitutionTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableInstitutions>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_INSTITUTIONS);

			// Filter here!
			filter(from, filters);

			List<ViewTableInstitutions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableInstitutions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
