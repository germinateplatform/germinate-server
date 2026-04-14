package jhi.germinate.server.resource.countries;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.CountryCount;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Countries.COUNTRIES;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;

@Path("country")
@Secured
@PermitAll
public class CountryResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCountries()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.select(
											  COUNTRIES.ID,
											  COUNTRIES.COUNTRY_NAME,
											  COUNTRIES.COUNTRY_CODE2,
											  COUNTRIES.COUNTRY_CODE3,
											  DSL.selectCount().from(GERMINATEBASE).leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(GERMINATEBASE.LOCATION_ID)).where(LOCATIONS.COUNTRY_ID.eq(COUNTRIES.ID)).asField("count")
									  ).from(COUNTRIES)
									  .fetchInto(CountryCount.class))
						   .build();
		}
	}
}
