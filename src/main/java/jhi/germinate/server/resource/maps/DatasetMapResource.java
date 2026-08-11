package jhi.germinate.server.resource.maps;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.MAPDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;

@Path("dataset/map")
@Secured
@PermitAll
public class DatasetMapResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableDatasetMaps> postDatasetMaps(DatasetRequest request)
			throws SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, null, request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Long totalMarkers = context.select(DSL.countDistinct(DATASETMEMBERS.FOREIGN_ID))
			                           .from(DATASETMEMBERS)
			                           .where(DATASETMEMBERS.DATASET_ID.in(requestedIds))
			                           .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
			                           .fetchAnyInto(Long.class);

			List<ViewTableDatasetMaps> maps = context.selectDistinct(
															 MAPS.ID.as("map_id"),
															 MAPS.NAME.as("map_name"),
															 MAPS.DESCRIPTION.as("map_description"),
															 MAPS.USER_ID.as("user_id"),
															 MAPS.VISIBILITY.as("visibility"),
															 DSL.countDistinct(MAPDEFINITIONS.MARKER_ID).as("map_coverage_count")
													 )
			                                         .from(MAPS)
			                                         .leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MAP_ID.eq(MAPS.ID))
			                                         .leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MAPDEFINITIONS.MARKER_ID)).and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
			                                         .where(MAPS.VISIBILITY.eq(true).or(MAPS.USER_ID.eq(userDetails.getId())))
			                                         .and(DATASETMEMBERS.DATASET_ID.in(requestedIds))
			                                         .groupBy(MAPS.ID)
			                                         .fetchInto(ViewTableDatasetMaps.class);

			for (ViewTableDatasetMaps map : maps)
				map.setMarkerCount(totalMarkers);

			return maps;
		}
	}
}
