package jhi.germinate.server.resource.stats;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PhenotypesDatatype;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Treatments.TREATMENTS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

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

		List<ViewTableDatasets> datasetsForUser = AuthorizationFilter.getDatasets(req, userDetails, "trials", true);
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
																				.leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
																				.where(TRIALSETUP.DATASET_ID.in(requestedDatasetIds))
																				.and(PHENOTYPEDATA.PHENOTYPE_ID.eq(VIEW_TABLE_TRAITS.TRAIT_ID)));

			if (!CollectionUtils.isEmpty(request.getXIds()))
				step.and(VIEW_TABLE_TRAITS.TRAIT_ID.in(request.getXIds()));

			Map<Integer, ViewTableTraits> traitMap = step.fetchMap(VIEW_TABLE_TRAITS.TRAIT_ID, ViewTableTraits.class);
			Map<Integer, ViewTableDatasets> datasetMap = datasetsForUser.stream()
																		.collect(Collectors.toMap(ViewTableDatasets::getDatasetId, Function.identity()));
			Map<Integer, Treatments> treatmentMap = new HashMap<>();
			context.selectFrom(TREATMENTS).forEach(t -> treatmentMap.put(t.getId(), t.into(Treatments.class)));

			Map<String, Quantiles> stats = new TreeMap<>();

			TempStats tempStats = new TempStats();
			DataType<BigDecimal> dt = SQLDataType.DECIMAL(64, 10);

			Field<String> groupIds = CollectionUtils.isEmpty(request.getYGroupIds())
					? DSL.inline(null, SQLDataType.VARCHAR).as("groupIds")
					: DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
						 .from(GROUPMEMBERS)
						 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
						 .where(GROUPMEMBERS.GROUP_ID.in(request.getYGroupIds()))
						 .and(GROUPMEMBERS.FOREIGN_ID.eq(TRIALSETUP.GERMINATEBASE_ID)).asField("groupIds");

			// Run the query
			SelectOnConditionStep<Record5<Integer, Integer, Integer, String, BigDecimal>> dataStep = context.select(
																													TRIALSETUP.DATASET_ID,
																													PHENOTYPEDATA.PHENOTYPE_ID,
																													TRIALSETUP.TREATMENT_ID,
																													// Now, get the concatenated group names for the requested selection.
																													groupIds,
																													DSL.iif(PHENOTYPES.DATATYPE.ne(PhenotypesDatatype.numeric), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).cast(dt).as("phenotype_value")
																											)
																											.from(PHENOTYPEDATA)
																											.leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
																											.leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID));

			// Restrict to dataset ids and phenotype ids
			SelectConditionStep<Record5<Integer, Integer, Integer, String, BigDecimal>> condStep = dataStep.where(TRIALSETUP.DATASET_ID.in(requestedDatasetIds))
																										   .and(PHENOTYPEDATA.PHENOTYPE_ID.in(traitMap.keySet()));

			SelectLimitStep<Record5<Integer, Integer, Integer, String, BigDecimal>> orderByStep;

			// If a subselection was requested
			if (!CollectionUtils.isEmpty(request.getYGroupIds()) || !CollectionUtils.isEmpty(request.getYIds()))
			{
				// Then restrict this here to only the ones in the groups. We'll get the marked ones further down
				Condition groups = DSL.exists(DSL.selectOne().from(GROUPS.leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPS.ID.in(request.getYGroupIds())).and(GROUPMEMBERS.FOREIGN_ID.eq(TRIALSETUP.GERMINATEBASE_ID))));

				orderByStep = condStep.and(groups)
									  .groupBy(PHENOTYPEDATA.ID)
									  .having(groupIds.isNotNull())
									  .orderBy(groupIds, PHENOTYPEDATA.PHENOTYPE_ID, TRIALSETUP.TREATMENT_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, dt));
			}
			else
			{
				// If nothing specific was requested, order by dataset instead
				orderByStep = dataStep.orderBy(TRIALSETUP.DATASET_ID, PHENOTYPEDATA.PHENOTYPE_ID, TRIALSETUP.TREATMENT_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, dt));
			}

			boolean isGroupQuery = !CollectionUtils.isEmpty(request.getYGroupIds());

			// This consumes the database result and generates the stats
			Consumer<Record5<Integer, Integer, Integer, String, BigDecimal>> consumer = pd -> {
				Integer datasetId = pd.get(TRIALSETUP.DATASET_ID);
				Integer traitId = pd.get(PHENOTYPEDATA.PHENOTYPE_ID);
				Integer treatmentId = pd.get(TRIALSETUP.TREATMENT_ID);
				String groupId = pd.get(groupIds);

				String key;

				if (isGroupQuery)
					key = "null|" + treatmentId + "|" + traitId + "|" + groupId;
				else
					key = datasetId + "|" + treatmentId + "|" + traitId + "|null";

//				String key = datasetId + "|" + traitId + "|" + groupId;
				BigDecimal value = pd.get("phenotype_value", BigDecimal.class);

				if (!Objects.equals(key, tempStats.prev))
				{
					if (tempStats.prev != null)
					{
						stats.put(tempStats.prev, generateStats(tempStats));
					}

					tempStats.datasetId = datasetId;
					tempStats.treatmentId = treatmentId;
					tempStats.groupIds = groupId;
					tempStats.avg = 0;
					tempStats.count = 0;
					tempStats.prev = key;
					tempStats.values.clear();
				}

				// Count in any case
				tempStats.count++;
				tempStats.avg += value.doubleValue();
				tempStats.values.add(value.doubleValue());
			};

			// Now stream the result and consume it
			orderByStep.stream()
					   .forEachOrdered(consumer);

			// If marked items were requested, then get these as well separately
			if (!CollectionUtils.isEmpty(request.getYIds()))
			{
				context.select(
							   TRIALSETUP.DATASET_ID,
							   PHENOTYPEDATA.PHENOTYPE_ID,
							   TRIALSETUP.TREATMENT_ID,
							   DSL.inline("Marked items").as("groupIds"),
							   DSL.iif(PHENOTYPES.DATATYPE.ne(PhenotypesDatatype.numeric), "0", PHENOTYPEDATA.PHENOTYPE_VALUE).cast(dt).as("phenotype_value")
					   )
					   .from(PHENOTYPEDATA)
					   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
					   .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
					   .where(TRIALSETUP.DATASET_ID.in(requestedDatasetIds))
					   .and(PHENOTYPEDATA.PHENOTYPE_ID.in(traitMap.keySet()))
					   .and(TRIALSETUP.GERMINATEBASE_ID.in(request.getYIds()))
					   .orderBy(PHENOTYPEDATA.PHENOTYPE_ID, DSL.cast(PHENOTYPEDATA.PHENOTYPE_VALUE, dt))
					   .forEach(consumer);
			}


			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, generateStats(tempStats));

			TraitDatasetStats result = new TraitDatasetStats();
			Set<ViewTableTraits> traits = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();
			Set<Treatments> treatments = new LinkedHashSet<>();

			result.setStats(stats.entrySet().stream()
								 .map(entry -> {
									 String[] split = entry.getKey().split("\\|");
//									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer datasetId = entry.getValue().getDatasetId();
									 Integer treatmentId = entry.getValue().getTreatmentId();
									 Integer traitId = Integer.parseInt(split[2]);
//									 String groupId = split[2];
									 String groupId = entry.getValue().getGroupIds();

									 traits.add(traitMap.get(traitId));
									 datasets.add(datasetMap.get(datasetId));
									 treatments.add(treatmentMap.get(treatmentId));

									 Quantiles q = stats.get(entry.getKey());
									 q.setTreatmentId(treatmentId);
									 q.setDatasetId(datasetId);
									 q.setXId(traitId);
									 q.setGroupIds(groupId);

									 return q;
								 })
								 .collect(Collectors.toList()));

			result.setDatasets(datasets);
			result.setTraits(traits);
			result.setTreatments(treatments);

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

		q.setDatasetId(tempStats.datasetId);
		q.setGroupIds(tempStats.groupIds);
		q.setTreatmentId(tempStats.treatmentId);

		return q;
	}

	static class TempStats
	{
		public String       prev   = null;
		public List<Double> values = new ArrayList<>();
		public double       avg    = 0;
		public int          count  = 0;
		public int          datasetId;
		public String       groupIds;
		public Integer      treatmentId;
	}
}
