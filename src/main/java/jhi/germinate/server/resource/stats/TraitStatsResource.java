package jhi.germinate.server.resource.stats;

import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.*;

@Path("dataset/stats/trial")
@Secured
@PermitAll
public class TraitStatsResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TraitDatasetStats postTraitStats(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<ViewTableDatasets> datasetsForUser = DatasetTableResource.getDatasetsForUser(req, resp, userDetails);
		List<Integer> requestedDatasetIds = CollectionUtils.isEmpty(request.getDatasetIds()) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		if (CollectionUtils.isEmpty(requestedDatasetIds))
			requestedDatasetIds = datasetsForUser.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList());
		else
			requestedDatasetIds.retainAll(datasetsForUser.stream()
														 .map(ViewTableDatasets::getDatasetId)
														 .collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(requestedDatasetIds))
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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

			// Run the query
			SelectOnConditionStep<Record4<Integer, Integer, String, String>> dataStep = context.select(
				PHENOTYPEDATA.DATASET_ID,
				PHENOTYPEDATA.PHENOTYPE_ID,
				// Now, get the concatenated group names for the requested selection.
				CollectionUtils.isEmpty(request.getyGroupIds())
					? DSL.inline(null, SQLDataType.VARCHAR).as("groupIds")
					: DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
						 .from(GROUPMEMBERS)
						 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
						 .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
						 .and(GROUPMEMBERS.FOREIGN_ID.eq(PHENOTYPEDATA.GERMINATEBASE_ID)).asField("groupIds"),
				DSL.iif(PHENOTYPES.DATATYPE.ne(PhenotypesDatatype.numeric), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).as("phenotype_value")
			)
																							   .from(PHENOTYPEDATA)
																							   .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID));

			// Restrict to dataset ids and phenotype ids
			SelectConditionStep<Record4<Integer, Integer, String, String>> condStep = dataStep.where(PHENOTYPEDATA.DATASET_ID.in(requestedDatasetIds))
																							  .and(PHENOTYPEDATA.PHENOTYPE_ID.in(traitMap.keySet()));

			SelectLimitStep<Record4<Integer, Integer, String, String>> orderByStep;

			// If a subselection was requested
			if (!CollectionUtils.isEmpty(request.getyGroupIds()) || !CollectionUtils.isEmpty(request.getyIds()))
			{
				// Then restrict this here to only the ones in the groups. We'll get the marked ones further down
				Condition groups = DSL.exists(DSL.selectOne().from(GROUPS.leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPS.ID.in(request.getyGroupIds())).and(GROUPMEMBERS.FOREIGN_ID.eq(PHENOTYPEDATA.GERMINATEBASE_ID))));

				orderByStep = condStep.and(groups)
									  .groupBy(PHENOTYPEDATA.ID)
									  .having(DSL.field("groupIds").isNotNull())
									  .orderBy(DSL.field("groupIds"), PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class));
			}
			else
			{
				// If nothing specific was requested, order by dataset instead
				orderByStep = dataStep.orderBy(PHENOTYPEDATA.DATASET_ID, PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class));
			}

			// This consumes the database result and generates the stats
			Consumer<Record4<Integer, Integer, String, String>> consumer = pd -> {
				Integer datasetId = pd.get(PHENOTYPEDATA.DATASET_ID);
				Integer traitId = pd.get(PHENOTYPEDATA.PHENOTYPE_ID);
				String groupIds = pd.get("groupIds", String.class);
				String key = datasetId + "|" + traitId + "|" + groupIds;
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
			};

			// Now stream the result and consume it
			orderByStep.stream()
					   .forEachOrdered(consumer);

			// If marked items were requested, then get these as well separately
			if (!CollectionUtils.isEmpty(request.getyIds()))
			{
				context.select(
					PHENOTYPEDATA.DATASET_ID,
					PHENOTYPEDATA.PHENOTYPE_ID,
					DSL.inline("Marked items").as("groupIds"),
					DSL.iif(PHENOTYPES.DATATYPE.ne(PhenotypesDatatype.numeric), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).as("phenotype_value")
				)
					   .from(PHENOTYPEDATA)
					   .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
					   .where(PHENOTYPEDATA.DATASET_ID.in(requestedDatasetIds))
					   .and(PHENOTYPEDATA.PHENOTYPE_ID.in(traitMap.keySet()))
					   .and(PHENOTYPEDATA.GERMINATEBASE_ID.in(request.getyIds()))
					   .orderBy(PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, Double.class))
					   .forEach(consumer);
			}


			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, generateStats(tempStats));

			TraitDatasetStats result = new TraitDatasetStats();
			Set<ViewTableTraits> traits = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split("\\|");
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
	}


	public static Quantiles generateStats(TempStats tempStats)
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
