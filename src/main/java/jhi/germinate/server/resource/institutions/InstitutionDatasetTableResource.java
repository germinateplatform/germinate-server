package jhi.germinate.server.resource.institutions;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableInstitutionDatasets;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableInstitutionDatasets.VIEW_TABLE_INSTITUTION_DATASETS;

@Path("institution/dataset/table")
@Secured
@PermitAll
public class InstitutionDatasetTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postInstitutionTable(PaginatedRequest request)
			throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null, false);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_INSTITUTION_DATASETS);

			// Filter here!
			where(from, filters, true);

			List<ViewTableInstitutionDatasets> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableInstitutionDatasets.class);

			result.forEach(d -> {
				d.setAllDatasetIds(restrictIds(d.getAllDatasetIds(), availableDatasets));
				d.setClimateDatasetIds(restrictIds(d.getClimateDatasetIds(), availableDatasets));
				d.setGenotypeDatasetIds(restrictIds(d.getGenotypeDatasetIds(), availableDatasets));
				d.setPedigreeDatasetIds(restrictIds(d.getPedigreeDatasetIds(), availableDatasets));
				d.setTrialsDatasetIds(restrictIds(d.getTrialsDatasetIds(), availableDatasets));
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return Response.ok(new PaginatedResult<>(result, count)).build();
		}
	}

	private Integer[] restrictIds(Integer[] dbIds, List<Integer> restrictTo)
	{
		if (dbIds != null)
		{
			List<Integer> ids = new ArrayList<>(Arrays.asList(dbIds));
			ids.retainAll(restrictTo);
			return ids.toArray(new Integer[0]);
		}
		else
		{
			return null;
		}
	}
}
