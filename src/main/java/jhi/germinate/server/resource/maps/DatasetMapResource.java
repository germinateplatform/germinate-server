package jhi.germinate.server.resource.maps;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMaps;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;

@Path("dataset/map")
@Secured
@PermitAll
public class DatasetMapResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableMaps> postDatasetMaps(DatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, null);
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectDistinct(
				MAPS.ID.as("map_id"),
				MAPS.NAME.as("map_name"),
				MAPS.DESCRIPTION.as("map_description"),
				MAPS.USER_ID.as("user_id"),
				MAPS.VISIBILITY.as("visibility"),
				DSL.countDistinct(MAPDEFINITIONS.MARKER_ID).as("marker_count")
			)
						  .from(MAPS)
						  .leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MAP_ID.eq(MAPS.ID))
						  .leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MAPDEFINITIONS.MARKER_ID))
						  .where(MAPS.VISIBILITY.eq(true).or(MAPS.USER_ID.eq(userDetails.getId())))
						  .and(DATASETMEMBERS.DATASET_ID.in(requestedIds))
						  .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
						  .groupBy(MAPS.ID)
						  .fetch()
						  .into(ViewTableMaps.class);
		}
	}
}
