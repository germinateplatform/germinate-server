package jhi.germinate.server.resource.stats;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.enums.PhenotypesDatatype;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Phenotypedata.*;
import static jhi.germinate.server.database.tables.Phenotypes.*;
import static jhi.germinate.server.database.tables.ViewTableTraits.*;

/**
 * @author Sebastian Raubach
 */
public class TraitStatsResource extends ServerResource
{
	@Post("json")
	public TraitDatasetStats postJson(PaginatedDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<ViewTableDatasets> datasetsForUser = DatasetTableResource.getDatasetsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasetsForUser.stream()
											  .map(ViewTableDatasets::getDatasetId)
											  .collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// All traits within the selected datasets
			Map<Integer, ViewTableTraits> traitMap = context.selectFrom(VIEW_TABLE_TRAITS)
															.whereExists(DSL.selectOne()
																			.from(PHENOTYPEDATA)
																			.where(PHENOTYPEDATA.DATASET_ID.in(requestedIds))
																			.and(PHENOTYPEDATA.PHENOTYPE_ID.eq(VIEW_TABLE_TRAITS.TRAIT_ID)))
															.fetchMap(VIEW_TABLE_TRAITS.TRAIT_ID, ViewTableTraits.class);

			Map<Integer, ViewTableDatasets> datasetMap = datasetsForUser.stream()
																		.collect(Collectors.toMap(ViewTableDatasets::getDatasetId, Function.identity()));

			Map<String, Quantiles> stats = new TreeMap<>();

			TempStats tempStats = new TempStats();

			context.select(
				PHENOTYPEDATA.DATASET_ID,
				PHENOTYPEDATA.PHENOTYPE_ID,
				DSL.iif(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.char_), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).as("phenotype_value")
			)
				   .from(PHENOTYPEDATA).leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
				   .where(PHENOTYPEDATA.DATASET_ID.in(requestedIds))
				   .orderBy(PHENOTYPEDATA.DATASET_ID, PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class))
				   .stream()
				   .forEachOrdered(pd -> {
					   Integer datasetId = pd.get(PHENOTYPEDATA.DATASET_ID);
					   Integer traitId = pd.get(PHENOTYPEDATA.PHENOTYPE_ID);
					   String key = datasetId + "," + traitId;
					   String value = pd.get("phenotype_value", String.class);

					   if (!Objects.equals(key, tempStats.prev))
					   {
						   if (tempStats.prev != null)
						   {
							   stats.put(tempStats.prev, generateStats(tempStats));
						   }

						   tempStats.avg = 0;
						   tempStats.count = 0;
						   tempStats.prev = key;
						   tempStats.values.clear();
					   }

					   // Count in any case
					   tempStats.count++;
					   try
					   {
						   double v = Double.parseDouble(value);
						   tempStats.avg += v;
						   tempStats.values.add(v);
					   }
					   catch (NumberFormatException | NullPointerException e)
					   {
					   }
				   });

			// Add the last one
			stats.put(tempStats.prev, generateStats(tempStats));

			TraitDatasetStats result = new TraitDatasetStats();
			Set<ViewTableTraits> traits = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split(",");
									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer traitId = Integer.parseInt(split[1]);

									 traits.add(traitMap.get(traitId));
									 datasets.add(datasetMap.get(datasetId));

									 Quantiles q = stats.get(ids);
									 q.setDatasetId(datasetId);
									 q.setTraitId(traitId);

									 return q;
								 })
								 .collect(Collectors.toList()));

			result.setDatasets(datasets);
			result.setTraits(traits);

			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private Quantiles generateStats(TempStats tempStats)
	{
		Quantiles q = new Quantiles();
		q.setCount(tempStats.count);

		if (tempStats.values.size() > 0)
		{
			q.setMin(tempStats.values.get(0));
			q.setAvg(tempStats.avg / tempStats.values.size());
			q.setMax(tempStats.values.get(tempStats.values.size() - 1));

			if (tempStats.values.size() > 1)
			{
				// If there is more than one value, calculate the median
				int index = tempStats.values.size() / 2;
				if (tempStats.values.size() % 2 == 0)
				{
					q.setMedian((tempStats.values.get(index) + tempStats.values.get(index - 1)) / 2.0f);
				}
				else
				{
					q.setMedian(tempStats.values.get(index));
				}

				q.setQ1(tempStats.values.get((int) Math.floor(tempStats.values.size() / 4f)));
				q.setQ3(tempStats.values.get((int) Math.floor(tempStats.values.size() / 4f * 3f)));
			}
			else
			{
				// If there's only one value, just use it
				q.setMedian(tempStats.values.get(0));
				q.setQ1(tempStats.values.get(0));
				q.setQ3(tempStats.values.get(0));
			}
		}

		return q;
	}

	private static class TempStats
	{
		private String       prev   = null;
		private List<Double> values = new ArrayList<>();
		private float        avg    = 0;
		private int          count  = 0;
	}
}
