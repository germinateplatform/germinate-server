package jhi.germinate.server.resource.stats;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewStatsOverview;

import static jhi.germinate.server.database.codegen.tables.ViewStatsOverview.*;

/**
 * @author Sebastian Raubach
 */
public class OverviewStatsResource extends ServerResource
{
	@Get("json")
	public ViewStatsOverview getJson()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.selectFrom(VIEW_STATS_OVERVIEW)
						  .fetchSingleInto(ViewStatsOverview.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
