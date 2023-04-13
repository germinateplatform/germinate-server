package jhi.germinate.server.resource.traits;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Phenotypes;
import jhi.germinate.server.database.codegen.tables.records.PhenotypesRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;

@Path("trait/{traitId:\\d+}")
public class TraitResource extends ContextResource
{
	@PathParam("traitId")
	private Integer traitId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean patchTrait(Phenotypes updatedTrait)
		throws SQLException, IOException
	{
		if (updatedTrait == null || StringUtils.isEmpty(updatedTrait.getName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			PhenotypesRecord trait = context.selectFrom(PHENOTYPES).where(PHENOTYPES.ID.eq(traitId)).fetchAny();

			if (trait == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			trait.setName(updatedTrait.getName());
			trait.setShortName(updatedTrait.getShortName());
			trait.setDescription(updatedTrait.getDescription());
			trait.setDatatype(updatedTrait.getDatatype());
			if (updatedTrait.getRestrictions() != null)
				trait.setRestrictions(updatedTrait.getRestrictions());
			if (updatedTrait.getUnitId() != null)
				trait.setUnitId(updatedTrait.getUnitId());
			return trait.store() > 0;
		}
	}

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

			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "trials");

			return context.selectDistinct(PHENOTYPEDATA.PHENOTYPE_VALUE).from(PHENOTYPEDATA)
						  .where(PHENOTYPEDATA.DATASET_ID.in(datasets))
						  .and(PHENOTYPEDATA.PHENOTYPE_ID.eq(traitId))
						  .orderBy(PHENOTYPEDATA.PHENOTYPE_VALUE)
						  .fetchInto(String.class);
		}
	}
}
