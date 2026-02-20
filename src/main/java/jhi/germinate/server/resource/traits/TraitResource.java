package jhi.germinate.server.resource.traits;

import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("trait/{traitId:\\d+}")
public class TraitResource extends ContextResource
{
	@PathParam("traitId")
	private Integer traitId;

	@GET
	@Path("values")
	@PermitAll
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDistinctTraitValues()
		throws IOException, SQLException
	{
		if (traitId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Integer> datasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", true);

			return context.selectDistinct(PHENOTYPEDATA.PHENOTYPE_VALUE).from(PHENOTYPEDATA)
						  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
						  .where(TRIALSETUP.DATASET_ID.in(datasets))
						  .and(PHENOTYPEDATA.VARIABLE_ID.eq(traitId))
						  .orderBy(PHENOTYPEDATA.PHENOTYPE_VALUE)
						  .fetchInto(String.class);
		}
	}
}
