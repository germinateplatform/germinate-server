package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

@Path("climate/stats")
@PermitAll
@Secured
public class ClimateStatsResource extends ContextResource
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClimateStats getClimateOverviewStats()
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, "climate", false);

		ClimateStats result = new ClimateStats();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Field<Integer> year = DSL.year(CLIMATEDATA.RECORDING_DATE);
			Field<Integer> dpCount = DSL.count().as("count");
			Map<Integer, Integer> dpsPerYear = new LinkedHashMap<>();
			context.select(year, dpCount)
			       .from(CLIMATEDATA)
			       .where(year.isNotNull())
			       .and(CLIMATEDATA.DATASET_ID.in(datasetIds))
			       .groupBy(year)
			       .orderBy(year.asc())
			       .forEach(record -> dpsPerYear.put(record.get(year), record.get(dpCount)));

			Field<Integer> climateCount = DSL.countDistinct(CLIMATEDATA.CLIMATE_ID).as("count");
			Map<Integer, Integer> climatesPerYear = new LinkedHashMap<>();
			context.select(year, climateCount)
			       .from(CLIMATEDATA)
			       .where(CLIMATEDATA.DATASET_ID.in(datasetIds))
			       .and(year.isNotNull())
			       .groupBy(year)
			       .orderBy(year.asc())
			       .forEach(record -> climatesPerYear.put(record.get(year), record.get(climateCount)));

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
			result.setClimatesPerYear(climatesPerYear);
			result.setClimateDatasetsPerYear(dssPerYear);
		}


		return result;
	}

	@POST
	@Path("/year")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getClimateYears(ClimateDatasetRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();


		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "climate", request.getDatasetIds(), true);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectDistinct(DSL.year(CLIMATEDATA.RECORDING_DATE))
			                          .from(CLIMATEDATA)
			                          .where(CLIMATEDATA.DATASET_ID.in(datasetIds))
			                          .and(CLIMATEDATA.CLIMATE_ID.in(request.getClimateIds()))
			                          .and(CLIMATEDATA.RECORDING_DATE.isNotNull())
			                          .fetchInto(Integer.class);
		}
	}
}
