package jhi.germinate.server.resource.stats;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;

import static jhi.germinate.server.database.codegen.tables.ViewStatsCountry.*;

/**
 * @author Sebastian Raubach
 */
public class CountryStatsResource extends StatsResource
{
	@Get
	public FileRepresentation getJson()
	{
		return export("country", VIEW_STATS_COUNTRY);
	}
}
