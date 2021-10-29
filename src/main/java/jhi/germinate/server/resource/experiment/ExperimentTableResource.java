package jhi.germinate.server.resource.experiment;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Experiments.*;

@Path("experiment/table")
@Secured
@PermitAll
public class ExperimentTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableExperiments>> getJson(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<? extends Record> select = context.select(
				EXPERIMENTS.ID.as("experiment_id"),
				EXPERIMENTS.EXPERIMENT_NAME.as("experiment_name"),
				EXPERIMENTS.DESCRIPTION.as("experiment_description"),
				EXPERIMENTS.EXPERIMENT_DATE.as("experiment_date"),
				EXPERIMENTS.CREATED_ON.as("created_on"),
				DSL.zero().as("genotype_count"),
				DSL.zero().as("trials_count"),
				DSL.zero().as("allele_freq_count"),
				DSL.zero().as("climate_count"),
				DSL.zero().as("compound_count")
			);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<? extends Record> from = select.from(EXPERIMENTS);

			// Filter here!
			filter(from, filters);

			List<ViewTableExperiments> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableExperiments.class);

			List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetsForUser(req, resp, userDetails, false);

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
							case "compound":
								experiment.setCompoundCount(experiment.getCompoundCount() + 1);
								break;
						}
					}
				}
			}

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
