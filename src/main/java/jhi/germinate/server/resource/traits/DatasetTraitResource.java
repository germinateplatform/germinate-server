package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

@Path("dataset/trait")
@Secured
@PermitAll
public class DatasetTraitResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableTraits> postDatasetTraits(DatasetRequest request)
			throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(VIEW_TABLE_TRAITS)
						  .where(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA)
											   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
											   .where(TRIALSETUP.DATASET_ID.in(requestedIds))
											   .and(PHENOTYPEDATA.VARIABLE_ID.eq(VIEW_TABLE_TRAITS.VARIABLE_ID))
											   .limit(1)))
						  .orderBy(VIEW_TABLE_TRAITS.VARIABLE_NAME)
						  .fetchInto(ViewTableTraits.class);
		}
	}
}
