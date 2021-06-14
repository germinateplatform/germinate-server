package jhi.germinate.server.resource.maps;

import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;

@Path("map/{mapId}/chromosome")
@Secured
@PermitAll
public class MapChromosomeResource extends ContextResource
{
	@PathParam("mapId")
	private Integer mapId;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getMapChromosomes()
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (mapId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectDistinct(MAPDEFINITIONS.CHROMOSOME)
						  .from(MAPS)
						  .leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MAP_ID.eq(MAPS.ID))
						  .where(MAPS.ID.eq(mapId))
						  .and(MAPS.VISIBILITY.eq(true)
											  .or(MAPS.USER_ID.eq(userDetails.getId())))
						  .orderBy(MAPDEFINITIONS.CHROMOSOME)
						  .fetchInto(String.class);
		}
	}
}
