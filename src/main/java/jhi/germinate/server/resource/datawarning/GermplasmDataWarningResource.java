package jhi.germinate.server.resource.datawarning;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Datawarnings;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Datawarnings.DATAWARNINGS;
import static jhi.germinate.server.database.codegen.tables.Germplasmdatawarnings.GERMPLASMDATAWARNINGS;

@Path("datawarning/germplasm/{germplasmId:\\d+}")
@Secured
@PermitAll
public class GermplasmDataWarningResource
{
	@PathParam("germplasmId")
	Integer germplasmId;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGermplasmDataWarning()
			throws SQLException
	{
		if (germplasmId == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.select(DATAWARNINGS.fields())
									  .from(GERMPLASMDATAWARNINGS)
									  .leftJoin(DATAWARNINGS).on(DATAWARNINGS.ID.eq(GERMPLASMDATAWARNINGS.DATAWARNING_ID))
									  .where(GERMPLASMDATAWARNINGS.GERMINATEBASE_ID.eq(germplasmId))
									  .fetchInto(Datawarnings.class))
						   .build();
		}
	}
}
