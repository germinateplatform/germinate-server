package jhi.germinate.server.resource.stats;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;

import static jhi.germinate.server.database.codegen.tables.ViewStatsTaxonomy.*;

/**
 * @author Sebastian Raubach
 */
public class TaxonomyStatsResource extends StatsResource
{
	@Get
	public FileRepresentation getJson()
	{
		return export("taxonomy", VIEW_STATS_TAXONOMY);
	}
}
