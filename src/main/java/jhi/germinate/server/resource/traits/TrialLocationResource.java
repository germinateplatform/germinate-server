package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;

@Path("dataset/data/trial/location")
@Secured
@PermitAll
public class TrialLocationResource extends ContextResource
{
	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public long postTrialLocationCount(DatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null) {
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return 0;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "trials");
		List<Integer> requestedIds;

		if (CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			requestedIds = datasets;
		}
		else
		{
			requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
			requestedIds.retainAll(datasets);
		}

		if (CollectionUtils.isEmpty(requestedIds))
			return 0;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectCount()
						  .from(DSL.selectDistinct(PHENOTYPEDATA.LATITUDE, PHENOTYPEDATA.LONGITUDE)
								   .from(PHENOTYPEDATA)
								   .where(PHENOTYPEDATA.DATASET_ID.in(requestedIds))
								   .and(PHENOTYPEDATA.LATITUDE.isNotNull())
								   .and(PHENOTYPEDATA.LONGITUDE.isNotNull())
								   .asTable())
						  .fetchOneInto(Long.class);
		}
	}
}
