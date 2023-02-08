package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableFileresources;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableFileresources.*;

@Path("dataset/fileresource")
@Secured
@PermitAll
public class DatasetFileResourceTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableFileresources>> postDatasetFileResources(PaginatedDatasetRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null);
		List<Integer> requestedIds = CollectionUtils.isEmpty(request.getDatasetIds()) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasetIds);

		// None of the requested dataset ids are available to the user, return nothing
		if (CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_FILERESOURCES)
													 // Filter on the referenced dataset ids
													 .where(DSL.exists(DSL.selectOne()
																		  .from(DATASETFILERESOURCES)
																		  .where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(VIEW_TABLE_FILERESOURCES.FILERESOURCE_ID)
																													 .and(DATASETFILERESOURCES.DATASET_ID.in(requestedIds)))));

			// Filter here!
			filter(from, filters);

			List<ViewTableFileresources> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableFileresources.class);

			result.forEach(r -> {
				if (!CollectionUtils.isEmpty(r.getDatasetIds()))
				{
					// Remove all dataset ids they don't have access to
					List<Integer> ids = new ArrayList<>(Arrays.asList(r.getDatasetIds()));
					ids.retainAll(datasetIds);
					r.setDatasetIds(ids.toArray(new Integer[0]));
				}
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
