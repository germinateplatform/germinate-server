package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.ViewMcpd;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Mcpd.*;

@Path("germplasm/{germplasmId}/mcpd")
@Secured
@PermitAll
public class GermplasmMcpdResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ViewMcpd getGermplasmMcpd(@PathParam("germplasmId") Integer germplasmId)
		throws IOException, SQLException
	{
		if (germplasmId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Field<?>> fields = new ArrayList<>(Arrays.asList(MCPD.fields()));
			fields.add(MCPD.GERMINATEBASE_ID.as("id"));

			return context.select(fields)
						  .from(MCPD)
						  .where(MCPD.GERMINATEBASE_ID.eq(germplasmId))
						  .fetchAnyInto(ViewMcpd.class);
		}
	}
}
