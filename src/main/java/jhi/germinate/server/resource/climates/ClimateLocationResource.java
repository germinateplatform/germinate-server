package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

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
	public List<ViewTableLocations> getClimateLocations(DatasetRequest request)
			throws SQLException
	{
		if (request == null)
			throw new BadRequestException();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(VIEW_TABLE_LOCATIONS)
			              .whereExists(DSL.selectOne()
			                              .from(CLIMATEDATA)
			                              .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
			                              .and(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID)))
			              .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull())
			              .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
			              .fetchInto(ViewTableLocations.class);
		}
	}

	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Long postClimateLocationCount(DatasetRequest request)
			throws SQLException
	{
		if (request == null)
			throw new BadRequestException();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return 0L;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectCount()
			              .from(DSL.selectDistinct(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE, VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE)
			                       .from(CLIMATEDATA)
			                       .leftJoin(VIEW_TABLE_LOCATIONS).on(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
			                       .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
			                       .and(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.isNotNull())
			                       .and(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.isNotNull())
			                       .asTable())
			              .fetchOneInto(Long.class);
		}
	}
}
