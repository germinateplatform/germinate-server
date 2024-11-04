package jhi.germinate.server.resource.mapoverlay;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMapoverlays;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapoverlays.*;

@Path("mapoverlay/table")
@Secured
@PermitAll
public class MapOverlayTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableMapoverlays>> getJson(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null, true);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_MAPOVERLAYS)
													 // Restrict to visible datasets
													 .where(VIEW_TABLE_MAPOVERLAYS.DATASET_ID.isNull().or(VIEW_TABLE_MAPOVERLAYS.DATASET_ID.in(datasetIds)));

			// Filter here!
			where(from, filters);

			List<ViewTableMapoverlays> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableMapoverlays.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
