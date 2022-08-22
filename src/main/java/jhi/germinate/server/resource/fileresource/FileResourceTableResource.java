package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
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

@Path("fileresource/table")
@Secured
@PermitAll
public class FileResourceTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableFileresources>> getJson(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, null);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_FILERESOURCES)
													 // Check whether this resource either isn't linked to any dataset
													 .where(DSL.notExists(DSL.selectOne()
																			 .from(DATASETFILERESOURCES)
																			 .where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(VIEW_TABLE_FILERESOURCES.FILERESOURCE_ID)))
															   // Or whether the user has access to the dataset
															   .or(DSL.exists(DSL.selectOne()
																				 .from(DATASETFILERESOURCES)
																				 .where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(VIEW_TABLE_FILERESOURCES.FILERESOURCE_ID)
																															.and(DATASETFILERESOURCES.DATASET_ID.in(datasetIds))))));

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
