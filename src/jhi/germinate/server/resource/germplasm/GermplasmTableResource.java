package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.ViewTableGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableGermplasm>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GERMPLASM);

			if (filters != null && filters.length > 0)
			{
				Filter filter = filters[0];
				from.where(DSL.field(filter.getColumn()).eq(filter.getValues()[0]));
			}

			if (ascending != null && orderBy != null)
			{
				// Camelcase to underscore
				orderBy = orderBy.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();

				if (ascending)
					from.orderBy(DSL.field(orderBy).asc());
				else
					from.orderBy(DSL.field(orderBy).desc());
			}

			List<ViewTableGermplasm> result = from.limit(pageSize)
												  .offset(pageSize * currentPage)
												  .fetch()
												  .into(ViewTableGermplasm.class);

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
