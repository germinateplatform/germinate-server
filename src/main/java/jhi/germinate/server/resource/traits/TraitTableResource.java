package jhi.germinate.server.resource.traits;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.resource.Post;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.*;

/**
 * @author Sebastian Raubach
 */
public class TraitTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableTraits>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
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
	{
		try (DSLContext context = Database.getContext())
		{
			return context.select()
						  .from(VIEW_TABLE_TRAITS)
						  .where(DSL.condition("JSON_CONTAINS(" + VIEW_TABLE_TRAITS.DATASET_IDS.getName() + ", '" + datasetId + "')"))
						  .fetchInto(ViewTableTraits.class);
		}
	}
}
