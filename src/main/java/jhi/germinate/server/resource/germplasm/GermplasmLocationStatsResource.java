package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;

@Path("germplasm/location")
@Secured
@PermitAll
public class GermplasmLocationStatsResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<DbObjectCount> getGermplasmLocations()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
