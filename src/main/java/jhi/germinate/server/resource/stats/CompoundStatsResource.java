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
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.SubsettedServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.tables.Compounddata.*;
import static jhi.germinate.server.database.tables.Compounds.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Groupmembers.*;
import static jhi.germinate.server.database.tables.Groups.*;
import static jhi.germinate.server.database.tables.ViewTableCompounds.*;

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

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// All traits within the selected datasets
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

			SelectConditionStep<? extends Record> dataStep = context.select(
				COMPOUNDDATA.DATASET_ID,
				COMPOUNDDATA.COMPOUND_ID,
				// Now, get the concatenated group names for the requested selection.
				DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
				   .from(GROUPMEMBERS)
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
				   .and(GROUPMEMBERS.FOREIGN_ID.eq(COMPOUNDDATA.GERMINATEBASE_ID)).asField("groupIds"),
				COMPOUNDDATA.COMPOUND_VALUE
			)
																	.from(COMPOUNDDATA).leftJoin(COMPOUNDS).on(COMPOUNDS.ID.eq(COMPOUNDDATA.COMPOUND_ID))
																	.where(COMPOUNDDATA.DATASET_ID.in(requestedDatasetIds))
																	.and(COMPOUNDDATA.COMPOUND_ID.in(compoundMap.keySet()));

			SelectSeekStep3<? extends Record, ?, ?, ?> orderByStep;
			if (!CollectionUtils.isEmpty(request.getyGroupIds()) || !CollectionUtils.isEmpty(request.getyIds()))
			{
				Set<Integer> germplasmIds = getYIds(context, GERMINATEBASE, GERMINATEBASE.ID, request);

				// If something was requested, but no germplasm found, throw an exception
				if (CollectionUtils.isEmpty(germplasmIds))
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

				orderByStep = dataStep.and(COMPOUNDDATA.GERMINATEBASE_ID.in(germplasmIds))
									  .orderBy(DSL.field("groupIds"), COMPOUNDDATA.COMPOUND_ID, COMPOUNDDATA.COMPOUND_VALUE);
			}
			else
			{
				orderByStep = dataStep.orderBy(COMPOUNDDATA.DATASET_ID, COMPOUNDDATA.COMPOUND_ID, COMPOUNDDATA.COMPOUND_VALUE);
			}

			orderByStep.stream()
					   .forEachOrdered(pd -> {
						   Integer datasetId = pd.get(COMPOUNDDATA.DATASET_ID);
						   Integer traitId = pd.get(COMPOUNDDATA.COMPOUND_ID);
						   String groupIds = pd.get("groupIds", String.class);
						   String key = datasetId + "," + traitId + "," + groupIds;
						   float value = pd.get(COMPOUNDDATA.COMPOUND_VALUE).floatValue();

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
						   tempStats.avg += value;
						   tempStats.values.add(value);
					   });

			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, generateStats(tempStats));

			CompoundDatasetStats result = new CompoundDatasetStats();
			Set<ViewTableCompounds> compounds = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split(",");
									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer traitId = Integer.parseInt(split[1]);
									 String groupIds = split[2];

									 compounds.add(compoundMap.get(traitId));
									 datasets.add(datasetMap.get(datasetId));

									 Quantiles q = stats.get(ids);
									 q.setDatasetId(datasetId);
									 q.setxId(traitId);
									 q.setGroupIds(groupIds);

									 return q;
								 })
								 .collect(Collectors.toList()));

			result.setDatasets(datasets);
			result.setCompounds(compounds);

			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
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
