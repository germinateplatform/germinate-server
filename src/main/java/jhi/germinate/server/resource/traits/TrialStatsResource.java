package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.TrialStats;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("trial/stats")
@PermitAll
@Secured
public class TrialStatsResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTrialOverviewStats()
			throws SQLException, IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, "trials", false);

		TrialStats result = new TrialStats();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Field<Integer> year = DSL.year(PHENOTYPEDATA.RECORDING_DATE);
			Field<Integer> dpCount = DSL.count().as("count");
			Map<Integer, Integer> dpsPerYear = new LinkedHashMap<>();
			context.select(year, dpCount)
			       .from(PHENOTYPEDATA)
			       .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
			       .where(TRIALSETUP.DATASET_ID.in(datasetIds))
			       .and(year.isNotNull())
			       .groupBy(year)
			       .orderBy(year.asc())
			       .forEach(record -> dpsPerYear.put(record.get(year), record.get(dpCount)));

			Field<Integer> traitCount = DSL.countDistinct(PHENOTYPEDATA.VARIABLE_ID).as("count");
			Map<Integer, Integer> traitsPerYear = new LinkedHashMap<>();
			context.select(year, traitCount)
			       .from(PHENOTYPEDATA)
			       .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
			       .where(TRIALSETUP.DATASET_ID.in(datasetIds))
			       .and(year.isNotNull())
			       .groupBy(year)
			       .orderBy(year.asc())
			       .forEach(record -> traitsPerYear.put(record.get(year), record.get(traitCount)));
			;

			Field<Integer> dsYear = DSL.coalesce(DSL.year(DATASETS.DATE_START), DSL.year(DATASETS.CREATED_ON));
			Field<Integer> dsCount = DSL.count().as("count");
			Map<Integer, Integer> dssPerYear = new LinkedHashMap<>();
			context.select(dsYear, dsCount)
			       .from(DATASETS)
			       .where(DATASETS.ID.in(datasetIds))
			       .groupBy(dsYear)
			       .orderBy(dsYear.asc())
			       .forEach(record -> dssPerYear.put(record.get(dsYear), record.get(dsCount)));

			result.setDataPointsByYear(dpsPerYear);
			result.setTraitsPerYear(traitsPerYear);
			result.setTrialsDatasetsPerYear(dssPerYear);
		}


		return Response.ok(result).build();
	}
}
