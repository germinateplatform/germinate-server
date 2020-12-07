package jhi.germinate.server.resource.stats;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewStatsOverview;
import org.jooq.DSLContext;
import org.restlet.resource.*;

import static jhi.germinate.server.database.codegen.tables.ViewStatsOverview.*;

/**
 * @author Sebastian Raubach
 */
public class OverviewStatsResource extends ServerResource
{
	@Get("json")
	public ViewStatsOverview getJson()
	{
		try (DSLContext context = Database.getContext())
		{
			return context.selectFrom(VIEW_STATS_OVERVIEW)
						  .fetchSingleInto(ViewStatsOverview.class);
		}
	}
}
