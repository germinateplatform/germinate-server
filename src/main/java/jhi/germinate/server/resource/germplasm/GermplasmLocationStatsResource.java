package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Countries.COUNTRIES;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;

@Path("germplasm/location")
@Secured
@PermitAll
public class GermplasmLocationStatsResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGermplasmLocations()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return Response.ok(context.select(
											  COUNTRIES.COUNTRY_NAME.as("key"),
											  DSL.selectCount().from(GERMINATEBASE).leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(GERMINATEBASE.LOCATION_ID)).where(LOCATIONS.COUNTRY_ID.eq(COUNTRIES.ID)).asField("count")
									  )
			                          .from(COUNTRIES)
			                          .orderBy(COUNTRIES.COUNTRY_NAME)
			                          .fetchInto(DbObjectCount.class)).build();
		}
	}
}
