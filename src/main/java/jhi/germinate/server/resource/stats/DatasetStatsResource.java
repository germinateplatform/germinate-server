package jhi.germinate.server.resource.stats;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.io.*;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetmeta.DATASETMETA;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Datasettypes.DATASETTYPES;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;

@Path("stats/dataset")
@Secured
@PermitAll
public class DatasetStatsResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput getDatasetStats(@Context HttpServletResponse response)
			throws SQLException
	{
		List<Integer> availableDatasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, false);

		try
		{
			File file = ResourceUtils.createTempFile("datasets", ".tsv");
			boolean hasResult = false;

			try (Connection conn = Database.getConnection();
			     PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				Set<String> years = new TreeSet<>();
				Map<String, DatasetStats> datasetTypeToStats = new TreeMap<>();

				Field<String> theYear = DSL.field("IF(ISNULL(" + DATASETS.DATE_START.getName() + "), 'UNKNOWN', DATE_FORMAT({0}, {1}))", SQLDataType.VARCHAR, DATASETS.DATE_START, DSL.inline("%Y"));

				context.select(
							   DATASETTYPES.DESCRIPTION.as("dataset_type"),
							   theYear.as("the_year"),
							   DSL.sum(DATASETMETA.NR_OF_DATA_POINTS).as("nr_of_data_points")
					   )
				       .from(DATASETS)
				       .leftJoin(DATASETMETA).on(DATASETMETA.DATASET_ID.eq(DATASETS.ID))
				       .leftJoin(EXPERIMENTS).on(EXPERIMENTS.ID.eq(DATASETS.EXPERIMENT_ID))
				       .leftJoin(DATASETTYPES).on(DATASETTYPES.ID.eq(DATASETS.DATASETTYPE_ID))
				       .where(DATASETS.ID.in(availableDatasets))
				       .and(DATASETS.IS_EXTERNAL.eq(false))
				       .and(theYear.isNotNull())
				       .groupBy(DATASETTYPES.DESCRIPTION, theYear)
				       .orderBy(DATASETTYPES.DESCRIPTION, theYear)
				       .forEach(r -> {
						   String year = r.get("the_year", String.class);

						   if (!StringUtils.isEmpty(year))
						   {
							   years.add(year);

							   String datasetType = r.get("dataset_type", String.class);
							   BigDecimal value = r.get("nr_of_data_points", BigDecimal.class);

							   DatasetStats stats = datasetTypeToStats.get(datasetType);

							   if (stats == null)
								   stats = new DatasetStats(datasetType);

							   stats.yearToCount.put(year, value == null ? null : value.intValue());
							   datasetTypeToStats.put(datasetType, stats);
						   }
					   });

				bw.write("DatasetType\t");
				bw.write(String.join("\t", years));
				bw.write(ResourceUtils.CRLF);

				for (DatasetStats stat : datasetTypeToStats.values())
				{
					hasResult = true;

					bw.write(stat.datasetType);
					for (String year : years)
					{
						Integer value = stat.yearToCount.get(year);

						if (value == null)
							bw.write("\t");
						else
							bw.write("\t" + value);
					}

					bw.write(ResourceUtils.CRLF);
				}
			}

			if (hasResult)
			{
				return toStreamingResult(file, MediaType.TEXT_PLAIN, response);
			}
			else
			{
				file.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}

		return null;
	}

	private static class DatasetStats
	{
		private String               datasetType;
		private Map<String, Integer> yearToCount = new HashMap<>();

		private DatasetStats(String datasetType)
		{
			this.datasetType = datasetType;
		}

		@Override
		public String toString()
		{
			return "DatasetStats{" +
					"datasetType='" + datasetType + '\'' +
					", yearToCount=" + yearToCount +
					'}';
		}
	}
}
