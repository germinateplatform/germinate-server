package jhi.germinate.server.resource.markers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.GenotypeStats;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Datasetmeta.DATASETMETA;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

@Path("genotype/stats")
@PermitAll
@Secured
public class GenotypeStatsResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGenotypeOverviewStats()
			throws SQLException, IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, "genotype", false);

		GenotypeStats result = new GenotypeStats();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Field<Integer> dsYear = DSL.coalesce(DSL.year(DATASETS.DATE_START), DSL.year(DATASETS.CREATED_ON));
			Field<BigDecimal> dpCount = DSL.sum(DATASETMETA.NR_OF_DATA_POINTS);
			Map<Integer, BigDecimal> dpsPerYear = new LinkedHashMap<>();
			context.select(dsYear, dpCount)
			       .from(DATASETS)
			       .leftJoin(DATASETMETA).on(DATASETMETA.DATASET_ID.eq(DATASETS.ID))
			       .where(DATASETS.ID.in(datasetIds))
			       .and(dsYear.isNotNull())
			       .groupBy(dsYear)
			       .orderBy(dsYear.asc())
			       .forEach(record -> dpsPerYear.put(record.get(dsYear), record.get(dpCount)));

			Field<Integer> markerCount = DSL.countDistinct(DATASETMEMBERS.FOREIGN_ID).as("count");
			Map<Integer, Integer> markersPerYear = new LinkedHashMap<>();
			context.select(dsYear, markerCount)
			       .from(DATASETMEMBERS)
			       .leftJoin(DATASETS).on(DATASETS.ID.eq(DATASETMEMBERS.DATASET_ID))
			       .where(DATASETMEMBERS.DATASET_ID.in(datasetIds))
			       .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
			       .and(dsYear.isNotNull())
			       .groupBy(dsYear)
			       .orderBy(dsYear.asc())
			       .forEach(record -> markersPerYear.put(record.get(dsYear), record.get(markerCount)));
			;

			Field<Integer> dsCount = DSL.count().as("count");
			Map<Integer, Integer> dssPerYear = new LinkedHashMap<>();
			context.select(dsYear, dsCount)
			       .from(DATASETS)
			       .where(DATASETS.ID.in(datasetIds))
			       .groupBy(dsYear)
			       .orderBy(dsYear.asc())
			       .forEach(record -> dssPerYear.put(record.get(dsYear), record.get(dsCount)));

			result.setDataPointsByYear(dpsPerYear);
			result.setMarkersPerYear(markersPerYear);
			result.setGenotypeDatasetsPerYear(dssPerYear);
		}


		return Response.ok(result).build();
	}
}
