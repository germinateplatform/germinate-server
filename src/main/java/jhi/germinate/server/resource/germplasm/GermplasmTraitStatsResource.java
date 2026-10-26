package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.ViewTableTraitsScaleDatatype;
import jhi.germinate.server.database.codegen.tables.*;
import jhi.germinate.server.database.pojo.TraitRestrictions;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Traits.TRAITS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

@Path("germplasm/{germplasmId}/stats/trait")
@Secured
@PermitAll
public class GermplasmTraitStatsResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<GermplasmStats> getGermplasmTraitStats(@PathParam("germplasmId") Integer germplasmId, GermplasmExportRequest requestedSubset)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, "trials", true);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Phenotypedata p = PHENOTYPEDATA.as("p");
			Trialsetup ts = TRIALSETUP.as("ts");

			SelectConditionStep<?> min = context.select(DSL.min(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.leftJoin(ts).on(ts.ID.eq(p.TRIALSETUP_ID))
												.where(p.VARIABLE_ID.eq(PHENOTYPEDATA.VARIABLE_ID))
												.and(ts.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> max = context.select(DSL.max(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.leftJoin(ts).on(ts.ID.eq(p.TRIALSETUP_ID))
												.where(p.VARIABLE_ID.eq(PHENOTYPEDATA.VARIABLE_ID))
												.and(ts.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> count = context.select(DSL.count())
												  .from(p)
												  .leftJoin(ts).on(ts.ID.eq(p.TRIALSETUP_ID))
												  .where(p.VARIABLE_ID.eq(PHENOTYPEDATA.VARIABLE_ID))
												  .and(ts.DATASET_ID.in(datasetIds));

			if (requestedSubset != null && (!CollectionUtils.isEmpty(requestedSubset.getGroupIds()) || !CollectionUtils.isEmpty(requestedSubset.getIndividualIds())))
			{
				List<Integer> availableGroupIds = GroupResource.getGroupIdsForUser(userDetails, null);

				List<Integer> requestedGroupIds = requestedSubset.getGroupIds() == null ? new ArrayList<>() : new ArrayList<>(Arrays.stream(requestedSubset.getGroupIds()).toList());
				requestedGroupIds.retainAll(availableGroupIds);

				Set<Integer> allGermplasmIds = new HashSet<>();

				if (requestedSubset.getIndividualIds() != null)
					allGermplasmIds.addAll(Arrays.asList(requestedSubset.getIndividualIds()));

				if (!CollectionUtils.isEmpty(requestedGroupIds))
				{
					List<Integer> germplasmIds = context.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).where(GROUPMEMBERS.GROUP_ID.in(requestedGroupIds)).fetchInto(Integer.class);

					allGermplasmIds.addAll(germplasmIds);
				}

				if (!CollectionUtils.isEmpty(allGermplasmIds))
				{
					Condition condition = ts.GERMINATEBASE_ID.in(allGermplasmIds);

					min = min.and(condition);
					max = max.and(condition);
					count = count.and(condition);
				}
			}

			List<Integer> traitIds = new ArrayList<>();

			context.selectFrom(VIEW_TABLE_TRAITS)
				   .where(VIEW_TABLE_TRAITS.SCALE_DATATYPE.eq(ViewTableTraitsScaleDatatype.numeric))
				   .or(VIEW_TABLE_TRAITS.SCALE_DATATYPE.eq(ViewTableTraitsScaleDatatype.categorical))
				   .forEach(t -> {
					   if (t.get(VIEW_TABLE_TRAITS.SCALE_DATATYPE) == ViewTableTraitsScaleDatatype.numeric)
						   traitIds.add(t.get(VIEW_TABLE_TRAITS.VARIABLE_ID));
					   else if (t.get(VIEW_TABLE_TRAITS.SCALE_DATATYPE) == ViewTableTraitsScaleDatatype.categorical)
					   {
						   TraitRestrictions traitRestrictions = t.get(VIEW_TABLE_TRAITS.SCALE_RESTRICTIONS);

						   if (traitRestrictions != null && !CollectionUtils.isEmpty(traitRestrictions.getCategories()))
						   {
							   for (String[] scales : traitRestrictions.getCategories())
							   {
								   boolean allNumeric = true;

								   for (String value : scales)
									   allNumeric &= NumberUtils.isParsable(value);

								   if (allNumeric)
									   traitIds.add(t.get(VIEW_TABLE_TRAITS.VARIABLE_ID));
							   }

						   }
					   }
				   });

			return context.select(
								  GERMINATEBASE.ID.as("germplasm_id"),
								  GERMINATEBASE.NAME.as("germplasm_name"),
								  VARIABLES.ID.as("variable_id"),
								  VARIABLES.NAME.as("variable_name"),
								  TRAITS.ID.as("trait_id"),
								  TRAITS.NAME.as("trait_name"),
								  TRAITS.ABBREVIATION.as("trait_name_short"),
								  min.asField("min"),
								  DSL.avg(PHENOTYPEDATA.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))).as("avg"),
								  max.asField("max"),
								  count.asField("count")
						  ).from(PHENOTYPEDATA)
						  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
						  .leftJoin(VARIABLES).on(VARIABLES.ID.eq(PHENOTYPEDATA.VARIABLE_ID))
						  .leftJoin(TRAITS).on(TRAITS.ID.eq(VARIABLES.TRAIT_ID))
						  .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
						  .where(GERMINATEBASE.ID.eq(germplasmId))
						  .and(TRIALSETUP.DATASET_ID.in(datasetIds))
						  .and(VARIABLES.ID.in(traitIds))
						  .groupBy(VARIABLES.ID, GERMINATEBASE.ID)
						  .orderBy(VARIABLES.NAME)
						  .fetchInto(GermplasmStats.class);
		}
	}
}
