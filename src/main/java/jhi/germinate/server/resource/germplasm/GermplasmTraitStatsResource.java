package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.GermplasmStats;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.Phenotypedata;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;

public class GermplasmTraitStatsResource extends BaseServerResource
{
	private Integer germplasmId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.germplasmId = Integer.parseInt(getRequestAttributes().get("germplasmId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get
	public List<GermplasmStats> getGermplasmTraitStats()
	{
		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse(), true);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			Phenotypedata p = PHENOTYPEDATA.as("p");

			SelectConditionStep<?> min = context.select(DSL.min(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
												.from(p)
												.where(p.PHENOTYPE_ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
												.and(p.DATASET_ID.in(datasetIds));

			SelectConditionStep<?> max = context.select(DSL.max(p.PHENOTYPE_VALUE.cast(SQLDataType.DECIMAL.precision(64, 10))))
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
				DSL.count().as("count")
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
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
