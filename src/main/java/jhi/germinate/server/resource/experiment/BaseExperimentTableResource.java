package jhi.germinate.server.resource.experiment;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;

public class BaseExperimentTableResource extends ExportResource implements IFilteredResource
{
	public PaginatedResult<List<ViewTableExperiments>> runQuery(PaginatedRequest request, DatasetTableResource.AdjustQuery optionalAdjuster)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<?> select = context.select(DSL.asterisk());

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			Field<Integer> projectId = EXPERIMENTS.PROJECT_ID.as("project_id");

			SelectJoinStep<? extends Record> inner = context.select(
					projectId,
					EXPERIMENTS.ID.as("experiment_id"),
					EXPERIMENTS.EXPERIMENT_NAME.as("experiment_name"),
					EXPERIMENTS.DESCRIPTION.as("experiment_description"),
					EXPERIMENTS.EXPERIMENT_DATE.as("experiment_date"),
					EXPERIMENTS.CREATED_ON.as("created_on"),
					DSL.zero().as("genotype_count"),
					DSL.zero().as("trials_count"),
					DSL.zero().as("allele_freq_count"),
					DSL.zero().as("climate_count"),
					DSL.zero().as("pedigree_count")
			).from(EXPERIMENTS);

			SelectJoinStep<? extends Record> from = select.from(inner);

			if (optionalAdjuster != null)
				optionalAdjuster.adjustQuery(from);

			// Filter here!
			where(from, filters);

			List<ViewTableExperiments> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableExperiments.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			List<ViewTableDatasets> datasets = AuthorizationFilter.getDatasets(req, userDetails, null, false);

			for (ViewTableExperiments experiment : result)
			{
				for (ViewTableDatasets dataset : datasets)
				{
					if (!dataset.getIsExternal() && Objects.equals(dataset.getExperimentId(), experiment.getExperimentId()))
					{
						switch (dataset.getDatasetType())
						{
							case "genotype":
								experiment.setGenotypeCount(experiment.getGenotypeCount() + 1);
								break;
							case "trials":
								experiment.setTrialsCount(experiment.getTrialsCount() + 1);
								break;
							case "allelefreq":
								experiment.setAlleleFreqCount(experiment.getAlleleFreqCount() + 1);
								break;
							case "climate":
								experiment.setClimateCount(experiment.getClimateCount() + 1);
								break;
							case "pedigree":
								experiment.setPedigreeCount(experiment.getPedigreeCount() + 1);
								break;
						}
					}
				}
			}

			return new PaginatedResult<>(result, count);
		}
	}

	public interface AdjustQuery
	{
		void adjustQuery(SelectJoinStep<? extends Record> query);
	}
}
