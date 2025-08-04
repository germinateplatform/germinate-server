package jhi.germinate.server.resource.projects;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableProjects;
import jhi.germinate.server.database.pojo.Dataset;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableProjects.VIEW_TABLE_PROJECTS;

@Path("project/table")
@Secured
@PermitAll
public class ProjectTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableProjects>> postProjectTable(PaginatedRequest request)
			throws SQLException
	{
		HashSet<Integer> datasetsForUser = new HashSet<>(AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true));

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_PROJECTS);

			// Filter here!
			where(from, filters, true);

			List<ViewTableProjects> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableProjects.class);

			if (request.isMinimal())
			{
				result.forEach(p -> {
					p.setProjectPageContent(null);
					p.setDatasets(null);
				});
			}
			else
			{
				result.forEach(p -> {
					Dataset[] datasets = p.getDatasets();

					if (!CollectionUtils.isEmpty(datasets))
						p.setDatasets(Arrays.stream(datasets).filter(ds -> datasetsForUser.contains(ds.getDatasetId())).toArray(Dataset[]::new));
				});
			}

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postMarkerTableIds(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_PROJECTS.PROJECT_ID)
														   .from(VIEW_TABLE_PROJECTS);

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
