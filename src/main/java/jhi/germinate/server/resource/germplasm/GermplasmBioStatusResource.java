package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DbObjectCount;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Mcpd.*;

@Path("germplasm/biologicalstatus")
@Secured
@PermitAll
public class GermplasmBioStatusResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<DbObjectCount> getGermplasmBioStatus()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.select(
				BIOLOGICALSTATUS.SAMPSTAT.as("key"),
				DSL.selectCount().from(MCPD).where(MCPD.SAMPSTAT.eq(BIOLOGICALSTATUS.ID)).asField("count")
			)
						  .from(BIOLOGICALSTATUS)
						  .orderBy(BIOLOGICALSTATUS.SAMPSTAT)
						  .fetchInto(DbObjectCount.class);
		}
	}
}
