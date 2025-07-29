package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.GermplasmStats;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.*;
import jhi.germinate.server.database.pojo.TraitRestrictions;
import jhi.germinate.server.util.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

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

	@GET
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<GermplasmStats> getGermplasmTraitStats(@PathParam("germplasmId") Integer germplasmId)
			throws SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, "trials", true);

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

			List<Integer> traitIds = new ArrayList<>();

			context.selectFrom(PHENOTYPES)
				   .where(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.numeric))
				   .or(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.categorical))
				   .forEach(t -> {
					   if (t.get(PHENOTYPES.DATATYPE) == PhenotypesDatatype.numeric)
						   traitIds.add(t.get(PHENOTYPES.ID));
					   else if (t.get(PHENOTYPES.DATATYPE) == PhenotypesDatatype.categorical)
					   {
						   TraitRestrictions traitRestrictions = t.get(PHENOTYPES.RESTRICTIONS);

						   if (traitRestrictions != null && !CollectionUtils.isEmpty(traitRestrictions.getCategories()))
						   {
							   for (String[] scales : traitRestrictions.getCategories())
							   {
								   boolean allNumeric = true;

								   for (String value : scales)
									   allNumeric &= NumberUtils.isParsable(value);

								   if (allNumeric)
									   traitIds.add(t.get(PHENOTYPES.ID));
							   }

						   }
					   }
				   });

			return context.select(
								  GERMINATEBASE.ID.as("germplasm_id"),
								  GERMINATEBASE.NAME.as("germplasm_name"),
								  PHENOTYPES.ID.as("trait_id"),
								  PHENOTYPES.NAME.as("trait_name"),
								  PHENOTYPES.SHORT_NAME.as("trait_name_short"),
								  min.asField("min"),
								  DSL.avg(PHENOTYPEDATA.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))).as("avg"),
								  max.asField("max"),
								  count.asField("count")
						  ).from(PHENOTYPEDATA)
						  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
						  .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
						  .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
						  .where(GERMINATEBASE.ID.eq(germplasmId))
						  .and(TRIALSETUP.DATASET_ID.in(datasetIds))
						  .and(PHENOTYPES.ID.in(traitIds))
						  .groupBy(PHENOTYPES.ID, GERMINATEBASE.ID)
						  .orderBy(PHENOTYPES.NAME)
						  .fetchInto(GermplasmStats.class);
		}
	}
}
