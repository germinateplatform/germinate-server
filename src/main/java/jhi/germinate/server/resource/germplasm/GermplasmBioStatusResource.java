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

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.BIOLOGICALSTATUS;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;

@Path("germplasm/biologicalstatus")
@Secured
@PermitAll
public class GermplasmBioStatusResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGermplasmBioStatus()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return Response.ok(context.select(
											  BIOLOGICALSTATUS.SAMPSTAT.as("key"),
											  DSL.selectCount().from(MCPD).where(MCPD.SAMPSTAT.eq(BIOLOGICALSTATUS.ID)).asField("count")
									  )
			                          .from(BIOLOGICALSTATUS)
			                          .orderBy(BIOLOGICALSTATUS.SAMPSTAT)
			                          .fetchInto(DbObjectCount.class)).build();
		}
	}
}
