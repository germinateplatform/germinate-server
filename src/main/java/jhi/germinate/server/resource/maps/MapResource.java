package jhi.germinate.server.resource.maps;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.MapsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.MAPDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;
import static jhi.germinate.server.database.codegen.tables.ViewTableMaps.VIEW_TABLE_MAPS;

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
			where(from, filters);

			List<ViewTableMaps> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableMaps.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@DELETE
	@Path("/{mapId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMap(@PathParam("mapId") Integer mapId)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectConditionStep<MapsRecord> step = context.selectFrom(MAPS).where(MAPS.ID.eq(mapId));

			if (!userDetails.isAtLeast(UserType.ADMIN))
				step.and(MAPS.USER_ID.eq(userDetails.getId()));

			MapsRecord map = step.fetchAny();

			if (map != null)
			{
				map.delete();

				// Delete any marker that no longer has an association with a map OR is linked to a genotypic dataset.
				context.deleteFrom(MARKERS)
					   .whereNotExists(DSL.selectOne().from(MAPDEFINITIONS).where(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID)))
					   .andNotExists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1)).and(DATASETMEMBERS.FOREIGN_ID.eq(MARKERS.ID)))
					   .executeAsync();

				return Response.ok().build();
			}
			else
				return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/{mapId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Maps>> getMaps(@PathParam("mapId") Integer mapId)
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
