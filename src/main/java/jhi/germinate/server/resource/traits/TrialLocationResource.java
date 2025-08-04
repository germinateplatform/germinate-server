package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

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

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);
		if (CollectionUtils.isEmpty(requestedIds))
			return 0;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectCount()
						  .from(DSL.selectDistinct(TRIALSETUP.LATITUDE, TRIALSETUP.LONGITUDE)
								   .from(TRIALSETUP)
								   .where(TRIALSETUP.DATASET_ID.in(requestedIds))
								   .and(TRIALSETUP.LATITUDE.isNotNull())
								   .and(TRIALSETUP.LONGITUDE.isNotNull())
								   .asTable())
						  .fetchOneInto(Long.class);
		}
	}
}
