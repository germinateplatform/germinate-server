package jhi.germinate.server.resource.stats;

import jhi.germinate.resource.EntityTypeStats;
import jhi.germinate.server.Database;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Entitytypes.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class EntityTypeStatsResource extends ServerResource
{
	@Get("json")
	public List<EntityTypeStats> getJson()
	{
		try (DSLContext context = Database.getContext())
		{
			return context.select(
				ENTITYTYPES.ID.as("entity_type_id"),
				ENTITYTYPES.NAME.as("entity_type_name"),
				DSL.selectCount().from(GERMINATEBASE).where(GERMINATEBASE.ENTITYTYPE_ID.eq(ENTITYTYPES.ID)).asField("count"))
				   .from(ENTITYTYPES)
				   .fetchInto(EntityTypeStats.class);
		}
	}
}
