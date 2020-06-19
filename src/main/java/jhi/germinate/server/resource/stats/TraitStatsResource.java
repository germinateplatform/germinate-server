package jhi.germinate.server.resource.stats;

import org.jooq.*;
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
import jhi.germinate.server.resource.SubsettedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Groupmembers.*;
import static jhi.germinate.server.database.tables.Groups.*;
import static jhi.germinate.server.database.tables.Phenotypedata.*;
import static jhi.germinate.server.database.tables.Phenotypes.*;
import static jhi.germinate.server.database.tables.ViewTableTraits.*;

/**
 * @author Sebastian Raubach
 */
public class TraitStatsResource extends SubsettedServerResource
{
	@Post("json")
	public TraitDatasetStats postJson(SubsettedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<ViewTableDatasets> datasetsForUser = DatasetTableResource.getDatasetsForUser(getRequest(), getResponse());
		List<Integer> requestedDatasetIds = CollectionUtils.isEmpty(request.getDatasetIds()) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		if (CollectionUtils.isEmpty(requestedDatasetIds))
			requestedDatasetIds = datasetsForUser.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList());
		else
			requestedDatasetIds.retainAll(datasetsForUser.stream()
														 .map(ViewTableDatasets::getDatasetId)
														 .collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(requestedDatasetIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// All traits within the selected datasets
			SelectConditionStep<? extends Record> step = context.selectFrom(VIEW_TABLE_TRAITS)
																.whereExists(DSL.selectOne()
																				.from(PHENOTYPEDATA)
																				.where(PHENOTYPEDATA.DATASET_ID.in(requestedDatasetIds))
																				.and(PHENOTYPEDATA.PHENOTYPE_ID.eq(VIEW_TABLE_TRAITS.TRAIT_ID)));

			if (!CollectionUtils.isEmpty(request.getxIds()))
				step.and(VIEW_TABLE_TRAITS.TRAIT_ID.in(request.getxIds()));

			Map<Integer, ViewTableTraits> traitMap = step.fetchMap(VIEW_TABLE_TRAITS.TRAIT_ID, ViewTableTraits.class);
			Map<Integer, ViewTableDatasets> datasetMap = datasetsForUser.stream()
																		.collect(Collectors.toMap(ViewTableDatasets::getDatasetId, Function.identity()));

			Map<String, Quantiles> stats = new TreeMap<>();

			TempStats tempStats = new TempStats();

			SelectConditionStep<? extends Record> dataStep = context.select(
				PHENOTYPEDATA.DATASET_ID,
				PHENOTYPEDATA.PHENOTYPE_ID,
				// Now, get the concatenated group names for the requested selection.
				DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
				   .from(GROUPMEMBERS)
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
				   .and(GROUPMEMBERS.FOREIGN_ID.eq(PHENOTYPEDATA.GERMINATEBASE_ID)).asField("groupIds"),
				DSL.iif(PHENOTYPES.DATATYPE.eq(PhenotypesDatatype.char_), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).as("phenotype_value")
			)
																	.from(PHENOTYPEDATA).leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
																	.where(PHENOTYPEDATA.DATASET_ID.in(requestedDatasetIds))
																	.and(PHENOTYPEDATA.PHENOTYPE_ID.in(traitMap.keySet()));

			SelectSeekStep3<? extends Record, ?, ?, ?> orderByStep;
			if (!CollectionUtils.isEmpty(request.getyGroupIds()) || !CollectionUtils.isEmpty(request.getyIds()))
			{
				Set<Integer> germplasmIds = getYIds(context, GERMINATEBASE, GERMINATEBASE.ID, request);

				// If something was requested, but no germplasm found, throw an exception
				if (CollectionUtils.isEmpty(germplasmIds))
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

				orderByStep = dataStep.and(PHENOTYPEDATA.GERMINATEBASE_ID.in(germplasmIds))
									  .orderBy(DSL.field("groupIds"), PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class));
			}
			else
			{
				orderByStep = dataStep.orderBy(PHENOTYPEDATA.DATASET_ID, PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class));
			}

			orderByStep.stream()
					   .forEachOrdered(pd -> {
						   Integer datasetId = pd.get(PHENOTYPEDATA.DATASET_ID);
						   Integer traitId = pd.get(PHENOTYPEDATA.PHENOTYPE_ID);
						   String groupIds = pd.get("groupIds", String.class);
						   String key = datasetId + "," + traitId + "," + groupIds;
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
							   float v = Float.parseFloat(value);
							   tempStats.avg += v;
							   tempStats.values.add(v);
						   }
						   catch (NumberFormatException | NullPointerException e)
						   {
						   }
					   });

			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, generateStats(tempStats));

			TraitDatasetStats result = new TraitDatasetStats();
			Set<ViewTableTraits> traits = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split(",");
									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer traitId = Integer.parseInt(split[1]);
									 String groupIds = split[2];

									 traits.add(traitMap.get(traitId));
									 datasets.add(datasetMap.get(datasetId));

									 Quantiles q = stats.get(ids);
									 q.setDatasetId(datasetId);
									 q.setxId(traitId);
									 q.setGroupIds(groupIds);

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

	static class TempStats
	{
		public String      prev   = null;
		public List<Float> values = new ArrayList<>();
		public float       avg    = 0;
		public int         count  = 0;
	}
}
