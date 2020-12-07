package jhi.germinate.server.resource.stats;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.SubsettedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Compounds.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableCompounds.*;

/**
 * @author Sebastian Raubach
 */
public class CompoundStatsResource extends SubsettedServerResource
{
	@Post("json")
	public CompoundDatasetStats postJson(SubsettedDatasetRequest request)
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

		try (DSLContext context = Database.getContext())
		{
			// All compounds within the selected datasets
			SelectConditionStep<? extends Record> step = context.selectFrom(VIEW_TABLE_COMPOUNDS)
																.whereExists(DSL.selectOne()
																				.from(COMPOUNDDATA)
																				.where(COMPOUNDDATA.DATASET_ID.in(requestedDatasetIds))
																				.and(COMPOUNDDATA.COMPOUND_ID.eq(VIEW_TABLE_COMPOUNDS.COMPOUND_ID)));

			if (!CollectionUtils.isEmpty(request.getxIds()))
				step.and(VIEW_TABLE_COMPOUNDS.COMPOUND_ID.in(request.getxIds()));

			Map<Integer, ViewTableCompounds> compoundMap = step.fetchMap(VIEW_TABLE_COMPOUNDS.COMPOUND_ID, ViewTableCompounds.class);
			Map<Integer, ViewTableDatasets> datasetMap = datasetsForUser.stream()
																		.collect(Collectors.toMap(ViewTableDatasets::getDatasetId, Function.identity()));

			Map<String, Quantiles> stats = new TreeMap<>();

			TraitStatsResource.TempStats tempStats = new TraitStatsResource.TempStats();

			// Run the query
			SelectOnConditionStep<Record4<Integer, Integer, String, BigDecimal>> dataStep = context.select(
				COMPOUNDDATA.DATASET_ID,
				COMPOUNDDATA.COMPOUND_ID,
				// Now, get the concatenated group names for the requested selection.
				CollectionUtils.isEmpty(request.getyGroupIds())
					? DSL.inline(null, SQLDataType.VARCHAR).as("groupIds")
					: DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
						 .from(GROUPMEMBERS)
						 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
						 .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
						 .and(GROUPMEMBERS.FOREIGN_ID.eq(COMPOUNDDATA.GERMINATEBASE_ID)).asField("groupIds"),
				COMPOUNDDATA.COMPOUND_VALUE.as("compound_value")
			)
																								   .from(COMPOUNDDATA)
																								   .leftJoin(COMPOUNDS).on(COMPOUNDS.ID.eq(COMPOUNDDATA.COMPOUND_ID));

			// Restrict to dataset ids and compound ids
			SelectConditionStep<Record4<Integer, Integer, String, BigDecimal>> condStep = dataStep.where(COMPOUNDDATA.DATASET_ID.in(requestedDatasetIds))
																								  .and(COMPOUNDDATA.COMPOUND_ID.in(compoundMap.keySet()));

			SelectLimitStep<Record4<Integer, Integer, String, BigDecimal>> orderByStep;

			// If a subselection was requested
			if (!CollectionUtils.isEmpty(request.getyGroupIds()) || !CollectionUtils.isEmpty(request.getyIds()))
			{
				// Then restrict this here to only the ones in the groups. We'll get the marked ones further down
				Condition groups = DSL.exists(DSL.selectOne().from(GROUPS.leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPS.ID.in(request.getyGroupIds())).and(GROUPMEMBERS.FOREIGN_ID.eq(COMPOUNDDATA.GERMINATEBASE_ID))));

				orderByStep = condStep.and(groups)
									  .groupBy(COMPOUNDDATA.ID)
									  .having(DSL.field("groupIds").isNotNull())
									  .orderBy(DSL.field("groupIds"), COMPOUNDDATA.COMPOUND_ID, DSL.cast(COMPOUNDDATA.COMPOUND_VALUE, Double.class));
			}
			else
			{
				// If nothing specific was requested, order by dataset instead
				orderByStep = dataStep.orderBy(COMPOUNDDATA.DATASET_ID, COMPOUNDDATA.COMPOUND_ID, DSL.cast(COMPOUNDDATA.COMPOUND_VALUE, Double.class));
			}

			// This consumes the database result and generates the stats
			Consumer<Record4<Integer, Integer, String, BigDecimal>> consumer = pd -> {
				Integer datasetId = pd.get(COMPOUNDDATA.DATASET_ID);
				Integer compoundId = pd.get(COMPOUNDDATA.COMPOUND_ID);
				String groupIds = pd.get("groupIds", String.class);
				String key = datasetId + "|" + compoundId + "|" + groupIds;
				String value = pd.get("compound_value", String.class);

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
			};

			// Now stream the result and consume it
			orderByStep.stream()
					   .forEachOrdered(consumer);

			// If marked items were requested, then get these as well separately
			if (!CollectionUtils.isEmpty(request.getyIds()))
			{
				context.select(
					COMPOUNDDATA.DATASET_ID,
					COMPOUNDDATA.COMPOUND_ID,
					DSL.inline("Marked items").as("groupIds"),
					COMPOUNDDATA.COMPOUND_VALUE.as("compound_value")
				)
					   .from(COMPOUNDDATA)
					   .leftJoin(COMPOUNDS).on(COMPOUNDS.ID.eq(COMPOUNDDATA.COMPOUND_ID))
					   .where(COMPOUNDDATA.DATASET_ID.in(requestedDatasetIds))
					   .and(COMPOUNDDATA.COMPOUND_ID.in(compoundMap.keySet()))
					   .and(COMPOUNDDATA.GERMINATEBASE_ID.in(request.getyIds()))
					   .orderBy(COMPOUNDDATA.COMPOUND_ID, DSL.cast(COMPOUNDDATA.COMPOUND_VALUE, Double.class))
					   .forEach(consumer);
			}


			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, generateStats(tempStats));

			CompoundDatasetStats result = new CompoundDatasetStats();
			Set<ViewTableCompounds> compounds = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split("\\|");
									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer compoundId = Integer.parseInt(split[1]);
									 String groupIds = split[2];

									 compounds.add(compoundMap.get(compoundId));
									 datasets.add(datasetMap.get(datasetId));

									 Quantiles q = stats.get(ids);
									 q.setDatasetId(datasetId);
									 q.setxId(compoundId);
									 q.setGroupIds(groupIds);

									 return q;
								 })
								 .collect(Collectors.toList()));

			result.setDatasets(datasets);
			result.setCompounds(compounds);

			return result;
		}
	}

	private Quantiles generateStats(TraitStatsResource.TempStats tempStats)
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
}
