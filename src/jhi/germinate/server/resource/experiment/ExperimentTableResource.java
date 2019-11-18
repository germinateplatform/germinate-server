package jhi.germinate.server.resource.experiment;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableExperiments;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.ViewTableExperiments.*;

/**
 * @author Sebastian Raubach
 */
public class ExperimentTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableExperiments>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_EXPERIMENTS);

			// Filter here!
			filter(from, filters);

			List<ViewTableExperiments> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableExperiments.class);

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