package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Path("dataset/data/trial/germplasm")
@Secured
@PermitAll
public class TrialGermplasmResource extends TrialsDataBaseResource
{
	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableTrialsData>> postTrialGermplasm(PaginatedDatasetRequest request, @QueryParam("isGermplasm") @DefaultValue("false") Boolean isGermplasm)
		throws IOException, SQLException
	{
		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, "trials", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from;

			if (isGermplasm)
				from = getTrialsGermplasmDistinctNameQueryWrapped(context, null);
			else
				from = getTrialsGermplasmQueryWrapped(context, null);

			from.where(DSL.field(TrialsDataBaseResource.DATASET_ID, Integer.class).in(requestedIds));

			// Filter here!
			where(from, filters);

			List<ViewTableTrialsData> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableTrialsData.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
