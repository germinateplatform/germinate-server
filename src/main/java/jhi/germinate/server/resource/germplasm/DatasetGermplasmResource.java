package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("dataset/{datasetIdString:\\d+(?:,\\d+)*}/germplasm")
@Secured
@PermitAll
public class DatasetGermplasmResource extends GermplasmBaseResource
{
	@PathParam("datasetIdString")
	String datasetIdString;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGermplasm>> postDatasetGermplasm(PaginatedRequest request)
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

			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, minimal, null);

			// Restrict to only those locations that have climate data within the requested datasets
			from.having(
					DSL.exists(
							DSL.selectOne()
							   .from(PHENOTYPEDATA)
							   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
							   .where(TRIALSETUP.GERMINATEBASE_ID.eq(DSL.field(GERMPLASM_ID, Integer.class)))
							   .and(TRIALSETUP.DATASET_ID.in(availableIds))
					)
			);

			// Filter here!
			having(from, filters, true);

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}