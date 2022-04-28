package jhi.germinate.server.resource.maps;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Maps.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableMaps.*;

@Path("map")
@Secured
@PermitAll
public class MapResource extends BaseResource implements IFilteredResource
{
	@POST
	@Path("/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableMaps>> getJson(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MAPS);

			from.where(VIEW_TABLE_MAPS.VISIBILITY.eq(true)
												 .or(VIEW_TABLE_MAPS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<ViewTableMaps> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableMaps.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@GET
	@Path("/{mapId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Maps>> getMaps(@QueryParam("mapId") Integer mapId)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(MAPS);

			from.where(MAPS.VISIBILITY.eq(true)
									  .or(MAPS.USER_ID.eq(userDetails.getId())));

			if (mapId != null)
				from.where(MAPS.ID.eq(mapId));

			List<Maps> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Maps.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
