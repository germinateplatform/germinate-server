package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Synonyms.SYNONYMS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.Units.UNITS;
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

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "trials");

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
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.select(
				PHENOTYPES.ID.as(VIEW_TABLE_TRAITS.TRAIT_ID.getName()),
				PHENOTYPES.NAME.as(VIEW_TABLE_TRAITS.TRAIT_NAME.getName()),
				PHENOTYPES.SHORT_NAME.as(VIEW_TABLE_TRAITS.TRAIT_NAME_SHORT.getName()),
				PHENOTYPES.DESCRIPTION.as(VIEW_TABLE_TRAITS.TRAIT_DESCRIPTION.getName()),
				PHENOTYPES.DATATYPE.as(VIEW_TABLE_TRAITS.DATA_TYPE.getName()),
				PHENOTYPES.RESTRICTIONS.as(VIEW_TABLE_TRAITS.TRAIT_RESTRICTIONS.getName()),
				UNITS.ID.as(VIEW_TABLE_TRAITS.UNIT_ID.getName()),
				UNITS.UNIT_NAME.as(VIEW_TABLE_TRAITS.UNIT_NAME.getName()),
				UNITS.UNIT_DESCRIPTION.as(VIEW_TABLE_TRAITS.UNIT_DESCRIPTION.getName()),
				UNITS.UNIT_ABBREVIATION.as(VIEW_TABLE_TRAITS.UNIT_ABBREVIATION.getName()),
				SYNONYMS.SYNONYMS_.as(VIEW_TABLE_TRAITS.SYNONYMS.getName()),
				DSL.zero().as(VIEW_TABLE_TRAITS.COUNT.getName())
			)
						  .from(PHENOTYPES.leftJoin(UNITS).on(PHENOTYPES.UNIT_ID.eq(UNITS.ID))
										  .leftJoin(SYNONYMS).on(SYNONYMS.FOREIGN_ID.eq(PHENOTYPES.ID)
																					.and(SYNONYMS.SYNONYMTYPE_ID.eq(4))))
						  .where(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA)
											   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
											   .where(TRIALSETUP.DATASET_ID.in(requestedIds))
											   .and(PHENOTYPEDATA.PHENOTYPE_ID.eq(PHENOTYPES.ID))
											   .limit(1)))
						  .orderBy(PHENOTYPES.NAME)
						  .fetchInto(ViewTableTraits.class);
		}
	}
}
