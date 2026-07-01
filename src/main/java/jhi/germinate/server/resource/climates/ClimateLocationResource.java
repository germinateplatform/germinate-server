package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("dataset/data/climate/location")
@Secured
@PermitAll
public class ClimateLocationResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClimateLocations(DatasetRequest request)
			throws SQLException
	{
		if (request == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return Response.status(Response.Status.NOT_FOUND).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.selectFrom(VIEW_TABLE_LOCATIONS)
			                          .whereExists(DSL.selectOne()
			                                          .from(CLIMATEDATA)
			                                          .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
			                                          .and(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID)))
			                          .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull())
			                          .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
			                          .fetchInto(ViewTableLocations.class)).build();
		}
	}

	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postClimateLocationCount(DatasetRequest request)
			throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(0).build();
		}

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return Response.ok(0).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.selectCount()
			                          .from(DSL.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE, VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE)
			                                   .from(CLIMATEDATA)
			                                   .leftJoin(VIEW_TABLE_LOCATIONS).on(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
			                                   .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
			                                   .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull())
			                                   .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
			                                   .asTable())
			                          .fetchOneInto(Long.class)).build();
		}
	}
}
