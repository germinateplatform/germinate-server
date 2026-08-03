package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

@Path("germplasm/entity")
@Secured
@PermitAll
public class GermplasmEntityResource
{
	@QueryParam("direction")
	private String direction;

	@Context
	protected HttpServletResponse resp;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postGermplasmEntities(Integer[] ids)
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			if (ids == null)
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();

			if (Objects.equals(direction, "down"))
			{
				return Response.ok(context.selectDistinct(GERMINATEBASE.ID)
							  .from(GERMINATEBASE)
							  .where(GERMINATEBASE.ENTITYPARENT_ID.in(ids))
							  .fetchInto(Integer.class)).build();
			}
			else if (Objects.equals(direction, "up"))
			{
				return Response.ok(context.selectDistinct(GERMINATEBASE.ENTITYPARENT_ID)
							  .from(GERMINATEBASE)
							  .where(GERMINATEBASE.ENTITYPARENT_ID.isNotNull())
							  .and(GERMINATEBASE.ID.in(ids))
							  .fetchInto(Integer.class)).build();
			}
			else
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return null;
			}
		}
	}
}
