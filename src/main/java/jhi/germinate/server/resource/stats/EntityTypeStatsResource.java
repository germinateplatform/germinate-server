package jhi.germinate.server.resource.stats;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.germinate.resource.EntityTypeStats;
import jhi.germinate.server.Database;

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
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.select(
				ENTITYTYPES.ID.as("entity_type_id"),
				ENTITYTYPES.NAME.as("entity_type_name"),
				DSL.selectCount().from(GERMINATEBASE).where(GERMINATEBASE.ENTITYTYPE_ID.eq(ENTITYTYPES.ID)).asField("count"))
				   .from(ENTITYTYPES)
				   .fetchInto(EntityTypeStats.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
