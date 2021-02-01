package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmLocationResource extends ServerResource
{
	@Get("json")
	public List<DbObjectCount> getTaxonomies()
	{
		try (DSLContext context = Database.getContext())
		{
			return context.select(
				COUNTRIES.COUNTRY_NAME.as("key"),
				DSL.selectCount().from(GERMINATEBASE).leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(GERMINATEBASE.LOCATION_ID)).where(LOCATIONS.COUNTRY_ID.eq(COUNTRIES.ID)).asField("count")
			)
						  .from(COUNTRIES)
						  .orderBy(COUNTRIES.COUNTRY_NAME)
						  .fetchInto(DbObjectCount.class);
		}
	}
}
