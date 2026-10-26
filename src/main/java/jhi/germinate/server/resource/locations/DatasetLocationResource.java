package jhi.germinate.server.resource.locations;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("dataset/{datasetIdString:\\d+(?:,\\d+)*}/location")
@Secured
@PermitAll
public class DatasetLocationResource extends BaseResource
{
	@PathParam("datasetIdString")
	String datasetIdString;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableLocations>> postDatasetLocations(PaginatedRequest request)
			throws SQLException
	{
		List<Integer> datasetIds;

		try
		{
			datasetIds = new ArrayList<>(Arrays.stream(datasetIdString.split(",")).map(Integer::parseInt).toList());
		}
		catch (Exception e)
		{
			throw new BadRequestException();
		}

		List<Integer> availableIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, datasetIds, false);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<org.jooq.Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_LOCATIONS)
													 // Restrict to only those locations that have climate data within the requested datasets
													 .whereExists(DSL.selectOne()
																	 .from(CLIMATEDATA)
																	 .where(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
																	 .and(CLIMATEDATA.DATASET_ID.in(availableIds)));

			// Filter here!
			where(from, filters);

			List<ViewTableLocations> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableLocations.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}