package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

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
	public Response getTraitStats(TraitDatasetRequest request)
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
												.where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												.and(ts.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> max = context.select(DSL.max(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.leftJoin(ts).on(ts.ID.eq(p.TRIALSETUP_ID))
												.where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												.and(ts.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> count = context.select(DSL.count())
												  .from(p)
												  .leftJoin(ts).on(ts.ID.eq(p.TRIALSETUP_ID))
												  .where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												  .and(ts.DATASET_ID.in(datasetIds));

			List<TraitStats> numeric = context.select(
													  PHENOTYPES.ID.as("trait_id"),
													  PHENOTYPES.NAME.as("trait_name"),
													  PHENOTYPES.SHORT_NAME.as("trait_name_short"),
													  PHENOTYPES.DATATYPE.as("data_type"),
													  min.asField("min"),
													  DSL.avg(PHENOTYPEDATA.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))).as("avg"),
													  max.asField("max"),
													  count.asField("count")
											  ).from(PHENOTYPEDATA)
											  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
											  .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
											  .where(TRIALSETUP.DATASET_ID.in(datasetIds))
											  .and(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.numeric))
											  .and(PHENOTYPES.ID.in(request.getTraitIds()))
											  .groupBy(PHENOTYPES.ID)
											  .orderBy(PHENOTYPES.NAME)
											  .fetchInto(TraitStats.class);

			List<jhi.germinate.server.database.codegen.tables.pojos.Phenotypes> categorical = context.selectDistinct(PHENOTYPES.fields())
																									 .from(PHENOTYPEDATA)
																									 .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
																									 .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
																									 .where(TRIALSETUP.DATASET_ID.in(datasetIds))
																									 .and(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.categorical))
																									 .and(PHENOTYPES.RESTRICTIONS.isNotNull())
																									 .fetchInto(jhi.germinate.server.database.codegen.tables.pojos.Phenotypes.class);

			// Filter out the ones that don't have restriction categories
			categorical = categorical.stream().filter(t -> !CollectionUtils.isEmpty(t.getRestrictions().getCategories()) && !CollectionUtils.isEmpty(t.getRestrictions().getCategories()[0])).collect(Collectors.toList());

			Map<Integer, TraitStats> mapping = new HashMap<>();

			for (jhi.germinate.server.database.codegen.tables.pojos.Phenotypes t : categorical)
				mapping.put(t.getId(), new TraitStats().setTraitName(t.getName())
													   .setTraitNameShort(t.getShortName())
													   .setTraitId(t.getId())
													   .setDataType(t.getDatatype().getLiteral())
													   .setCategories(t.getRestrictions().getCategories())
													   .setMin(Double.MAX_VALUE)
													   .setMax(-Double.MAX_VALUE)
													   .setAvg(0d)
													   .setCount(0));

			context.selectDistinct()
				   .from(PHENOTYPEDATA)
				   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
				   .where(TRIALSETUP.DATASET_ID.in(datasetIds))
				   .and(PHENOTYPEDATA.PHENOTYPE_ID.in(mapping.keySet()))
				   .forEach(r -> {
					   TraitStats tStat = mapping.get(r.get(PHENOTYPEDATA.PHENOTYPE_ID));

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
			return Response.ok(numeric).build();
		}
	}
}
