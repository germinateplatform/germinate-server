package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import org.jooq.*;
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
			Field<Integer> count = DSL.count().as("count");
			return context.select(COUNTRIES.COUNTRY_NAME.as("key"), count)
						  .from(COUNTRIES)
						  .leftJoin(LOCATIONS).on(LOCATIONS.COUNTRY_ID.eq(COUNTRIES.ID))
						  .leftJoin(GERMINATEBASE).on(GERMINATEBASE.LOCATION_ID.eq(LOCATIONS.ID))
						  .groupBy(COUNTRIES.ID)
						  .having(count.gt(0))
						  .fetchInto(DbObjectCount.class);
		}
	}
}
