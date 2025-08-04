package jhi.germinate.server.resource.stats;

import jakarta.ws.rs.Path;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.*;

@Path("dataset/stats/climate")
@Secured
@PermitAll
public class ClimateStatsResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClimateDatasetStats postClimateStats(SubsettedDatasetRequest request)
			throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<ViewTableDatasets> datasetsForUser = AuthorizationFilter.getDatasets(req, userDetails, "climate", true);
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
			// All climates within the selected datasets
			SelectConditionStep<? extends Record> step = context.selectFrom(VIEW_TABLE_CLIMATES)
																.whereExists(DSL.selectOne()
																				.from(CLIMATEDATA)
																				.where(CLIMATEDATA.DATASET_ID.in(requestedDatasetIds))
																				.and(CLIMATEDATA.CLIMATE_ID.eq(VIEW_TABLE_CLIMATES.CLIMATE_ID)));

			if (!CollectionUtils.isEmpty(request.getxIds()))
				step.and(VIEW_TABLE_CLIMATES.CLIMATE_ID.in(request.getxIds()));

			Map<Integer, ViewTableClimates> climateMap = step.fetchMap(VIEW_TABLE_CLIMATES.CLIMATE_ID, ViewTableClimates.class);
			Map<Integer, ViewTableDatasets> datasetMap = datasetsForUser.stream()
																		.collect(Collectors.toMap(ViewTableDatasets::getDatasetId, Function.identity()));

			Map<String, Quantiles> stats = new TreeMap<>();

			TraitStatsResource.TempStats tempStats = new TraitStatsResource.TempStats();
			DataType<BigDecimal> dt = SQLDataType.DECIMAL(64, 10);

			Field<String> groupIdsField = CollectionUtils.isEmpty(request.getyGroupIds())
					? DSL.inline(null, SQLDataType.VARCHAR).as("groupIds")
					: DSL.select(DSL.field("json_arrayagg(CONCAT(LEFT(groups.name, 10), IF(LENGTH(groups.name)>10, '...', '')))").cast(String.class))
						 .from(GROUPMEMBERS)
						 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
						 .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
						 .and(GROUPMEMBERS.FOREIGN_ID.eq(CLIMATEDATA.LOCATION_ID)).asField("groupIds");

			// Run the query
			SelectOnConditionStep<Record4<Integer, Integer, String, BigDecimal>> dataStep = context.select(
																										   CLIMATEDATA.DATASET_ID,
																										   CLIMATEDATA.CLIMATE_ID,
																										   // Now, get the concatenated group names for the requested selection.
																										   groupIdsField,
																										   DSL.iif(CLIMATES.DATATYPE.ne(ClimatesDatatype.numeric), "0", CLIMATEDATA.CLIMATE_VALUE).cast(dt).as("phenotype_value")
																								   )
																								   .from(CLIMATEDATA)
																								   .leftJoin(CLIMATES).on(CLIMATES.ID.eq(CLIMATEDATA.CLIMATE_ID));

			// Restrict to dataset ids and climate ids
			SelectConditionStep<Record4<Integer, Integer, String, BigDecimal>> condStep = dataStep.where(CLIMATEDATA.DATASET_ID.in(requestedDatasetIds))
																								  .and(CLIMATEDATA.CLIMATE_ID.in(climateMap.keySet()));

			SelectLimitStep<Record4<Integer, Integer, String, BigDecimal>> orderByStep;

			// If a subselection was requested
			if (!CollectionUtils.isEmpty(request.getyGroupIds()) || !CollectionUtils.isEmpty(request.getyIds()))
			{
				// Then restrict this here to only the ones in the groups. We'll get the marked ones further down
				Condition groups = DSL.exists(DSL.selectOne().from(GROUPS.leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.GROUPTYPE_ID.eq(1).and(GROUPS.ID.in(request.getyGroupIds())).and(GROUPMEMBERS.FOREIGN_ID.eq(CLIMATEDATA.LOCATION_ID))));

				orderByStep = condStep.and(groups)
									  .groupBy(CLIMATEDATA.ID)
									  .having(groupIdsField.isNotNull())
									  .orderBy(groupIdsField, CLIMATEDATA.CLIMATE_ID, CLIMATEDATA.CLIMATE_VALUE);
			}
			else
			{
				// If nothing specific was requested, order by dataset instead
				orderByStep = dataStep.orderBy(CLIMATEDATA.DATASET_ID, CLIMATEDATA.CLIMATE_ID, CLIMATEDATA.CLIMATE_VALUE);
			}

			// This consumes the database result and generates the stats
			Consumer<Record4<Integer, Integer, String, BigDecimal>> consumer = pd -> {
				Integer datasetId = pd.get(CLIMATEDATA.DATASET_ID);
				Integer climateId = pd.get(CLIMATEDATA.CLIMATE_ID);
				String groupIds = pd.get(groupIdsField);
				String key = datasetId + "|" + climateId + "|" + groupIds;
				BigDecimal value = pd.get("phenotype_value", BigDecimal.class);

				if (!Objects.equals(key, tempStats.prev))
				{
					if (tempStats.prev != null)
					{
						stats.put(tempStats.prev, TraitStatsResource.generateStats(tempStats));
					}

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
			if (!CollectionUtils.isEmpty(request.getyIds()))
			{
				context.select(
							   CLIMATEDATA.DATASET_ID,
							   CLIMATEDATA.CLIMATE_ID,
							   DSL.inline("Marked items").as("groupIds"),
							   DSL.iif(CLIMATES.DATATYPE.ne(ClimatesDatatype.numeric), "0", CLIMATEDATA.CLIMATE_VALUE).cast(dt).as("phenotype_value")
					   )
					   .from(CLIMATEDATA)
					   .leftJoin(CLIMATES).on(CLIMATES.ID.eq(CLIMATEDATA.CLIMATE_ID))
					   .where(CLIMATEDATA.DATASET_ID.in(requestedDatasetIds))
					   .and(CLIMATEDATA.CLIMATE_ID.in(climateMap.keySet()))
					   .and(CLIMATEDATA.LOCATION_ID.in(request.getyIds()))
					   .orderBy(CLIMATEDATA.CLIMATE_ID, CLIMATEDATA.CLIMATE_VALUE)
					   .forEach(consumer);
			}

			// Add the last one
			if (!StringUtils.isEmpty(tempStats.prev))
				stats.put(tempStats.prev, TraitStatsResource.generateStats(tempStats));

			ClimateDatasetStats result = new ClimateDatasetStats();
			Set<ViewTableClimates> climates = new LinkedHashSet<>();
			Set<ViewTableDatasets> datasets = new LinkedHashSet<>();

			result.setStats(stats.keySet().stream()
								 .map(ids -> {
									 String[] split = ids.split("\\|");
									 Integer datasetId = Integer.parseInt(split[0]);
									 Integer climateId = Integer.parseInt(split[1]);
									 String groupIds = split[2];

									 climates.add(climateMap.get(climateId));
									 datasets.add(datasetMap.get(datasetId));

									 Quantiles q = stats.get(ids);
									 q.setDatasetId(datasetId);
									 q.setxId(climateId);
									 q.setGroupIds(groupIds);

									 return q;
								 })
								 .collect(Collectors.toList()));

			result.setDatasets(datasets);
			result.setClimates(climates);

			return result;
		}
	}
}
