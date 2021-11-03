package jhi.germinate.server.resource.experiment;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.*;
import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.*;
import static jhi.germinate.server.database.codegen.tables.Experiments.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Institutions.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

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
			SelectSelectStep<?> select = context.select(DSL.asterisk());

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<? extends Record> inner = context.select(
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
			).from(EXPERIMENTS);

			SelectJoinStep<? extends Record> from = select.from(inner);

			// Filter here!
			filter(from, filters);

			Logger.getLogger("").info("SQL: " + from.getSQL(ParamType.INLINED));

			List<ViewTableExperiments> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableExperiments.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

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

			return new PaginatedResult<>(result, count);
		}
	}
}
