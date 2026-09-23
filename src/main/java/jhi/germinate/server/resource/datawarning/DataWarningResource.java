package jhi.germinate.server.resource.datawarning;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Datawarnings;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datawarnings.DATAWARNINGS;

@Path("datawarning")
@Secured
@PermitAll
public class DataWarningResource
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Datawarnings> getDataWarnings()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(DATAWARNINGS)
						  .fetchInto(Datawarnings.class);
		}
	}
}
