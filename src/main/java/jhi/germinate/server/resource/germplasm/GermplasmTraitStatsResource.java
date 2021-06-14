package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.GermplasmStats;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.Phenotypedata;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<GermplasmStats> getGermplasmTraitStats(@PathParam("germplasmId") Integer germplasmId)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, true);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Phenotypedata p = PHENOTYPEDATA.as("p");

			SelectConditionStep<?> min = context.select(DSL.min(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												.and(p.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> max = context.select(DSL.max(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												.and(p.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> count = context.select(DSL.count())
												  .from(p)
												  .where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												  .and(p.DATASET_ID.in(datasetIds));

			return context.select(
				GERMINATEBASE.ID.as("germplasm_id"),
				GERMINATEBASE.NAME.as("germplasm_name"),
				PHENOTYPES.ID.as("trait_id"),
				PHENOTYPES.NAME.as("trait_name"),
				min.asField("min"),
				DSL.avg(PHENOTYPEDATA.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))).as("avg"),
				max.asField("max"),
				count.asField("count")
			).from(PHENOTYPEDATA)
						  .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
						  .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(PHENOTYPEDATA.GERMINATEBASE_ID))
						  .where(GERMINATEBASE.ID.eq(germplasmId))
						  .and(PHENOTYPEDATA.DATASET_ID.in(datasetIds))
						  .and(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.numeric))
						  .groupBy(PHENOTYPES.ID)
						  .orderBy(PHENOTYPES.NAME)
						  .fetchInto(GermplasmStats.class);
		}
	}
}
