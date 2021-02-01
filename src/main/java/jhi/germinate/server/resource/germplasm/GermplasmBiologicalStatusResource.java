package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmBiologicalStatusResource extends ServerResource
{
	@Get("json")
	public List<DbObjectCount> getBiologicalStatus()
	{
		try (DSLContext context = Database.getContext())
		{
			return context.select(
				BIOLOGICALSTATUS.SAMPSTAT.as("key"),
				DSL.selectCount().from(GERMINATEBASE).where(GERMINATEBASE.BIOLOGICALSTATUS_ID.eq(BIOLOGICALSTATUS.ID)).asField("count")
			)
						  .from(BIOLOGICALSTATUS)
						  .orderBy(BIOLOGICALSTATUS.SAMPSTAT)
						  .fetchInto(DbObjectCount.class);
		}
	}
}
