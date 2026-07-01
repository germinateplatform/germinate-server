package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTrialLayouts;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableTrialLayouts.VIEW_TABLE_TRIAL_LAYOUTS;

@Path("dataset/data/trial/layout")
@Secured
@PermitAll
public class TrialLayoutResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTrialLayout(DatasetRequest request)
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

			return Response.ok(context.selectFrom(VIEW_TABLE_TRIAL_LAYOUTS)
			                          .where(VIEW_TABLE_TRIAL_LAYOUTS.DATASET_ID.in(requestedIds))
			                          .and(VIEW_TABLE_TRIAL_LAYOUTS.ROW.isNotNull())
			                          .and(VIEW_TABLE_TRIAL_LAYOUTS.COLUMN.isNotNull())
			                          .fetchInto(ViewTableTrialLayouts.class)
			).build();
		}
	}

	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postTrialLayoutCount(DatasetRequest request)
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

			Map<Integer, Integer> counts = new HashMap<>();

			Field<Integer> count = DSL.count().as("count");
			context.select(VIEW_TABLE_TRIAL_LAYOUTS.DATASET_ID, count)
			       .from(VIEW_TABLE_TRIAL_LAYOUTS)
			       .where(VIEW_TABLE_TRIAL_LAYOUTS.DATASET_ID.in(requestedIds))
			       .and(VIEW_TABLE_TRIAL_LAYOUTS.ROW.isNotNull())
			       .and(VIEW_TABLE_TRIAL_LAYOUTS.COLUMN.isNotNull())
			       .groupBy(VIEW_TABLE_TRIAL_LAYOUTS.DATASET_ID)
			       .forEach(row -> {
					   counts.put(row.get(VIEW_TABLE_TRIAL_LAYOUTS.DATASET_ID), row.get(count));
				   });

			return Response.ok(counts).build();
		}
	}
}
