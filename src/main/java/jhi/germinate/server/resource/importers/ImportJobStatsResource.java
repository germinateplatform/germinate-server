package jhi.germinate.server.resource.importers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImportJobs;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Fileresources.FILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.ViewTableImportJobs.VIEW_TABLE_IMPORT_JOBS;

@Path("import/stats")
@Secured
@PermitAll
public class ImportJobStatsResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableImportJobs>> getImportJobStats(PaginatedRequest request, @QueryParam("projectIds") List<Integer> projectIds)
			throws SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, false);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_IMPORT_JOBS);

			Field<JSON> datasetIdField = DSL.field("json_extract(" + VIEW_TABLE_IMPORT_JOBS.STATS.getName() + ", '$.datasetId')", JSON.class);
			Field<JSON> fileResourceIdField = DSL.field("json_extract(" + VIEW_TABLE_IMPORT_JOBS.STATS.getName() + ", '$.fileResourceId')", JSON.class);

			if (!CollectionUtils.isEmpty(projectIds))
			{
				// Restrict to only those dataset ids (that the user has access to) that are related to the selected projects
				datasetIds.retainAll(context.select(DATASETS.ID).from(DATASETS).leftJoin(EXPERIMENTS).on(EXPERIMENTS.ID.eq(DATASETS.EXPERIMENT_ID)).where(EXPERIMENTS.PROJECT_ID.in(projectIds)).fetchInto(Integer.class));
				List<Integer> fileResourceIds = context.select(FILERESOURCES.ID).from(FILERESOURCES).where(FILERESOURCES.PROJECT_ID.in(projectIds)).fetchInto(Integer.class);
				from.where(fileResourceIdField.isNull().or(fileResourceIdField.in(fileResourceIds)));
			}

			from.where(datasetIdField.isNull().or(datasetIdField.cast(String.class).in(datasetIds)));

			// Filter here!
			where(from, filters);

			List<ViewTableImportJobs> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableImportJobs.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
