package jhi.germinate.server.resource.traits;

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

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("dataset/data/trial/location")
@Secured
@PermitAll
public class TrialLocationResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTrialLocations(DatasetRequest request)
			throws SQLException
	{
		if (request == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return Response.status(Response.Status.NOT_FOUND).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.select(
											  GERMINATEBASE.DISPLAY_NAME.as(VIEW_TABLE_LOCATIONS.LOCATION_NAME.getName()),
											  DSL.avg(TRIALSETUP.LATITUDE).as(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE.getName()),
											  DSL.avg(TRIALSETUP.LONGITUDE).as(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE.getName())
									  )
			                          .from(TRIALSETUP)
			                          .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
			                          .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                          .and(TRIALSETUP.LATITUDE.isNotNull())
			                          .and(TRIALSETUP.LONGITUDE.isNotNull())
			                          .groupBy(TRIALSETUP.GERMINATEBASE_ID)
			                          .fetchInto(ViewTableLocations.class)).build();
		}
	}

	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postTrialLocationCount(DatasetRequest request)
			throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(0).build();
		}

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return Response.ok(0).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.selectCount()
			                          .from(DSL.selectDistinct(TRIALSETUP.LATITUDE, TRIALSETUP.LONGITUDE)
			                                   .from(TRIALSETUP)
			                                   .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                                   .and(TRIALSETUP.LATITUDE.isNotNull())
			                                   .and(TRIALSETUP.LONGITUDE.isNotNull())
			                                   .asTable())
			                          .fetchOneInto(Long.class)).build();
		}
	}
}
