package jhi.germinate.server.resource.biologicalstatus;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.BiologicalStatusCount;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.BIOLOGICALSTATUS;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;

@Path("biologicalstatus")
@Secured
@PermitAll
public class BiologicalStatusResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBiologicalStatus(@QueryParam("fullName") Boolean fullName)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.select(
											  BIOLOGICALSTATUS.ID,
											  ((fullName == null || !fullName) ? DSL.substringIndex(BIOLOGICALSTATUS.SAMPSTAT, "(", 1) : BIOLOGICALSTATUS.SAMPSTAT).as("biologicalstatus"),
											  DSL.count().as("count")
									  ).from(BIOLOGICALSTATUS)
									  .leftJoin(MCPD).on(MCPD.SAMPSTAT.eq(BIOLOGICALSTATUS.ID))
									  .groupBy(BIOLOGICALSTATUS)
									  .fetchInto(BiologicalStatusCount.class))
						   .build();
		}
	}
}
