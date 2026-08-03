package jhi.germinate.server.resource.germplasm;

import jakarta.ws.rs.Path;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

@Path("germplasm/external/ids")
@Secured
@PermitAll
public class GermplasmExternalIdResource
{
	@Context
	protected HttpServletResponse resp;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postExternalIds(Integer[] ids)
		throws IOException, SQLException
	{
		String identifier = PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER);

		if (StringUtils.isEmpty(identifier))
			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
		if (CollectionUtils.isEmpty(ids))
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Field<?> field = DSL.field(identifier);

			return Response.ok(context.selectDistinct(field)
						  .from(GERMINATEBASE)
						  .where(GERMINATEBASE.ID.in(ids))
						  .fetchInto(String.class)).build();
		}
	}
}
