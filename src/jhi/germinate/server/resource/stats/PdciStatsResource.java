package jhi.germinate.server.resource.stats;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;

import static jhi.germinate.server.database.tables.ViewStatsPdci.*;

/**
 * @author Sebastian Raubach
 */
public class PdciStatsResource extends StatsResource
{
	@Get
	public FileRepresentation getJson()
	{
		return export("pdci", VIEW_STATS_PDCI);
	}
}
