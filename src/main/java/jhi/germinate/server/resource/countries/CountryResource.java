package jhi.germinate.server.resource.countries;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Countries;
import jhi.germinate.server.util.Secured;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Countries.*;

@Path("country")
@Secured
@PermitAll
public class CountryResource
{
	@GET
	public List<Countries> getCountries()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			return Database.getContext(conn)
						   .selectFrom(COUNTRIES)
						   .fetchInto(Countries.class);
		}
	}
}
