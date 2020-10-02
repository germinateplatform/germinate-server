package jhi.germinate.server.resource.stats;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;

import static jhi.germinate.server.database.codegen.tables.ViewStatsBiologicalstatus.*;

/**
 * @author Sebastian Raubach
 */
public class BiologicalstatusStatsResource extends StatsResource
{
	@Get
	public FileRepresentation getJson()
	{
		return export("biologicalstatus", VIEW_STATS_BIOLOGICALSTATUS);
	}
}
