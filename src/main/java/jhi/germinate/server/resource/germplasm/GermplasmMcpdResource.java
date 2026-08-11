package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.ViewMcpd;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;

@Path("germplasm/{germplasmId}/mcpd")
@Secured
@PermitAll
public class GermplasmMcpdResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewMcpd getGermplasmMcpd(@PathParam("germplasmId") Integer germplasmId)
			throws SQLException
	{
		if (germplasmId == null)
			throw new BadRequestException();

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
