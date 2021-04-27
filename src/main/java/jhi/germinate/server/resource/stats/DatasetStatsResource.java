package jhi.germinate.server.resource.stats;

import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Datasetmeta.*;
import static jhi.germinate.server.database.tables.Datasets.*;
import static jhi.germinate.server.database.tables.Datasettypes.*;
import static jhi.germinate.server.database.tables.Experiments.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetStatsResource extends BaseServerResource
{
	@Get
	public FileRepresentation getJson()
	{
		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse(), false);

		FileRepresentation representation = null;

		try
		{
			File file = createTempFile("datasets", ".tsv");
			boolean hasResult = false;

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				Set<String> years = new TreeSet<>();
				Map<String, DatasetStats> datasetTypeToStats = new TreeMap<>();

				Field<String> theYear = DSL.field("IF(ISNULL({2}), 'UNKNOWN', DATE_FORMAT({0}, {1}))", SQLDataType.VARCHAR, DATASETS.DATE_START, DSL.inline("%Y"), DATASETS.DATE_START.getName());

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
				bw.write(CRLF);

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

					bw.write(CRLF);
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			if (hasResult)
			{
				representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
				representation.setSize(file.length());
				representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
			}
			else
			{
				file.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		return representation;
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
