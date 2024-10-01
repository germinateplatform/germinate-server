package jhi.germinate.server.resource.datasets;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableCollaborators.VIEW_TABLE_COLLABORATORS;

@Path("dataset/{datasetId}/collaborator")
@Secured
@PermitAll
public class DatasetCollaboratorTableResource extends BaseResource
{
	@PathParam("datasetId")
	private Integer datasetId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableCollaborators>> postCollaboratorTable(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, userDetails, false);

			if (dataset != null)
			{
				SelectSelectStep<Record> select = context.select();

				if (previousCount == -1)
					select.hint("SQL_CALC_FOUND_ROWS");

				SelectJoinStep<Record> from = select.from(VIEW_TABLE_COLLABORATORS);

				// Filter here!
				where(from, filters);

				from.where(VIEW_TABLE_COLLABORATORS.DATASET_ID.eq(datasetId));

				List<ViewTableCollaborators> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableCollaborators.class);

				long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

				return new PaginatedResult<>(result, count);
			}
			else
			{
				return new PaginatedResult<>(new ArrayList<>(), 0);
			}
		}
	}

	public static List<ViewTableCollaborators> getCollaboratorsForDataset(int datasetId, HttpServletRequest req, HttpServletResponse resp, AuthenticationFilter.UserDetails userDetails)
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, userDetails, false);

			if (dataset != null)
			{
				return context.select()
							  .from(VIEW_TABLE_COLLABORATORS)
							  .where(VIEW_TABLE_COLLABORATORS.DATASET_ID.eq(datasetId)).fetch()
							  .into(ViewTableCollaborators.class);
			}
			else
			{
				return null;
			}
		}
	}
}
