package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Methods.METHODS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Scales.SCALES;
import static jhi.germinate.server.database.codegen.tables.Traits.TRAITS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

@Path("trait/stats")
@Secured
@PermitAll
public class TraitStatsResource
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
	public List<TraitStats> getTraitStats(TraitDatasetRequest request)
			throws SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);

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

			List<TraitStats> numeric = context.select(
													  VARIABLES.ID.as("variable_id"),
													  VARIABLES.NAME.as("variable_name"),
													  TRAITS.ID.as("trait_id"),
													  TRAITS.NAME.as("trait_name"),
													  TRAITS.ABBREVIATION.as("trait_name_short"),
													  SCALES.DATATYPE.as("data_type"),
													  min.asField("min"),
													  DSL.avg(PHENOTYPEDATA.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))).as("avg"),
													  max.asField("max"),
													  count.asField("count")
											  ).from(PHENOTYPEDATA)
			                                  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
			                                  .leftJoin(VARIABLES).on(VARIABLES.ID.eq(PHENOTYPEDATA.VARIABLE_ID))
			                                  .leftJoin(METHODS).on(METHODS.ID.eq(VARIABLES.METHOD_ID))
			                                  .leftJoin(SCALES).on(SCALES.ID.eq(VARIABLES.SCALE_ID))
			                                  .leftJoin(TRAITS).on(TRAITS.ID.eq(VARIABLES.TRAIT_ID))
			                                  .where(TRIALSETUP.DATASET_ID.in(datasetIds))
			                                  .and(SCALES.DATATYPE.eq(ScalesDatatype.numeric))
			                                  .and(VARIABLES.ID.in(request.getTraitIds()))
			                                  .groupBy(VARIABLES.ID)
			                                  .orderBy(VARIABLES.NAME)
			                                  .fetchInto(TraitStats.class);


			List<jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits> categorical = context.select()
			                                                                                              .from(VIEW_TABLE_TRAITS)
			                                                                                              .whereExists(
																												  DSL.selectOne()
					                                                                                                 .from(PHENOTYPEDATA.leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)))
					                                                                                                 .where(TRIALSETUP.DATASET_ID.in(datasetIds))
					                                                                                                 .and(PHENOTYPEDATA.VARIABLE_ID.eq(VIEW_TABLE_TRAITS.VARIABLE_ID))
																										  )
			                                                                                              .and(VIEW_TABLE_TRAITS.VARIABLE_ID.in(request.getTraitIds()))
			                                                                                              .and(VIEW_TABLE_TRAITS.SCALE_DATATYPE.eq(ViewTableTraitsScaleDatatype.categorical))
			                                                                                              .and(VIEW_TABLE_TRAITS.SCALE_RESTRICTIONS.isNotNull())
			                                                                                              .fetchInto(jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits.class);

			Map<Integer, TraitStats> mapping = new HashMap<>();

			// Filter out the ones that don't have restriction categories
			categorical.stream().filter(t -> !CollectionUtils.isEmpty(t.getScaleRestrictions().getCategories()) && !CollectionUtils.isEmpty(t.getScaleRestrictions().getCategories()[0]))
			           .forEach(t -> {
						   mapping.put(t.getVariableId(), new TraitStats()
								   .setVariableId(t.getVariableId())
						           .setVariableName(t.getVariableName())
						           .setTraitName(t.getTraitName())
						           .setTraitNameShort(t.getTraitAbbreviation())
						           .setTraitId(t.getTraitId())
						           .setDataType(t.getScaleDatatype().getLiteral())
						           .setCategories(t.getScaleRestrictions().getCategories())
						           .setMin(Double.MAX_VALUE)
						           .setMax(-Double.MAX_VALUE)
						           .setAvg(0d)
						           .setCount(0));
					   });

			context.selectDistinct()
			       .from(PHENOTYPEDATA)
			       .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
			       .where(TRIALSETUP.DATASET_ID.in(datasetIds))
			       .and(PHENOTYPEDATA.VARIABLE_ID.in(mapping.keySet()))
			       .forEach(r -> {
					   TraitStats tStat = mapping.get(r.get(PHENOTYPEDATA.VARIABLE_ID));

					   String value = r.get(PHENOTYPEDATA.PHENOTYPE_VALUE);

					   int index = -1;

					   for (List<String> cat : tStat.getCategories())
					   {
						   if (index != -1)
							   break;

						   index = cat.indexOf(value);
					   }

					   if (index != -1)
					   {
						   tStat.setMin(Math.min(tStat.getMin(), index));
						   tStat.setMax(Math.max(tStat.getMax(), index));
						   tStat.setAvg(tStat.getAvg() + index);
						   tStat.setCount(tStat.getCount() + 1);
					   }
				   });

			for (TraitStats tStat : mapping.values())
				tStat.setAvg(tStat.getAvg() / tStat.getCount());

			numeric.addAll(mapping.values());
			return numeric;
		}
	}
}
