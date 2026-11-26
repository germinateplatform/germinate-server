package jhi.germinate.server.resource.datasets.export;

import com.google.gson.Gson;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.ViewTableClimatesDataType;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableClimates;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.VIEW_TABLE_CLIMATES;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

/**
 * Pulls long-format phenotype data (germplasm, variable, value) out of
 * PHENOTYPEDATA / TRIALSETUP and pivots it into a matrix of
 * germplasm (+ plot position) x variable.
 * <p>
 * Rows are merged together only when block, rep, trial_row and trial_column
 * all match - i.e. they come from the same physical plot. Numeric variables are
 * averaged across whatever contributes to that plot; everything else takes
 * the most recently recorded value, falling back to a majority vote.
 */
public class ClimateMatrixBuilder
{
	private Gson                                   gson;
	private Map<Integer, ViewTableClimates>        climates;
	private Map<Integer, ViewTableClimateLocation> locations;
	private AggregationMethod                      aggregationMethod = AggregationMethod.MEDIAN;

	/**
	 * Uniquely identifies a single plot instance. Rows are only ever merged within the same key.
	 */
	public record PlotKey(int locationId, Double latitude, Double longitude, Timestamp date, Integer year)
	{
	}

	/**
	 * One raw (climate, value, date) triple read from climatedata for a given plot.
	 */
	private record DataPoint(int climateId, String value, Timestamp recordingDate, int datasetId)
	{
	}

	/**
	 * Final pivoted result.
	 */
	public record ClimateMatrix(List<PlotKey> plotKeys,
								  Map<PlotKey, Map<Integer, String>> cells,
								  Map<PlotKey, Set<Integer>> datasetIds)
	{
	}

	public ClimateMatrixBuilder()
	{
		gson = new Gson();
	}

	public ClimateMatrixBuilder(AggregationMethod aggregationMethod)
	{
		this();
		this.aggregationMethod = aggregationMethod;
	}

	/**
	 * @param dsl     jOOQ context
	 * @param request the export request
	 */
	public ClimateMatrix buildMatrix(DSLContext dsl, ClimateExportDatasetRequest request)
	{
		// Get requested items
		climates = getClimates(dsl, request.getClimateIds());
		locations = getLocations(dsl, request.getLocationIds(), request.getLocationGroupIds(), request.getDatasetIds());

		Set<Integer> numericVariableIds = climates.keySet().stream().filter(variableId -> climates.get(variableId).getDataType() == ViewTableClimatesDataType.numeric).collect(Collectors.toSet());

		// Get the actual data
		Map<PlotKey, List<DataPoint>> byPlot = fetchRawData(dsl, request.getDatasetIds(), climates.keySet(), locations.keySet());

		List<PlotKey> plotKeys = new ArrayList<>(byPlot.keySet());
		Map<PlotKey, Map<Integer, String>> cells = new LinkedHashMap<>();
		Map<PlotKey, Set<Integer>> datasetIdsByPlot = new LinkedHashMap<>();

		for (Map.Entry<PlotKey, List<DataPoint>> entry : byPlot.entrySet())
		{
			Map<Integer, List<DataPoint>> byVariable = entry.getValue().stream()
															.collect(Collectors.groupingBy(DataPoint::climateId, LinkedHashMap::new, Collectors.toList()));

			Map<Integer, String> variableValues = new LinkedHashMap<>();
			for (Map.Entry<Integer, List<DataPoint>> variableEntry : byVariable.entrySet())
			{
				int variableId = variableEntry.getKey();
				List<DataPoint> points = variableEntry.getValue();

				String aggregated = numericVariableIds.contains(variableId)
						? aggregateNumeric(points)
						: aggregateNonNumeric(points);

				variableValues.put(variableId, aggregated);
			}

			cells.put(entry.getKey(), variableValues);

			Set<Integer> dsIds = entry.getValue().stream()
									  .map(DataPoint::datasetId)
									  .collect(Collectors.toCollection(LinkedHashSet::new));
			datasetIdsByPlot.put(entry.getKey(), dsIds);
		}

		return new ClimateMatrix(plotKeys, cells, datasetIdsByPlot);
	}

	// ------------------------------------------------------------------
	// Data access
	// ------------------------------------------------------------------
	private Map<Integer, ViewTableClimates> getClimates(DSLContext dsl, Integer[] climateIds)
	{
		return dsl.select()
				  .from(VIEW_TABLE_CLIMATES)
				  .where(VIEW_TABLE_CLIMATES.CLIMATE_ID.in(climateIds))
				  .fetchMap(VIEW_TABLE_CLIMATES.CLIMATE_ID, ViewTableClimates.class);
	}

	private Map<Integer, ViewTableClimateLocation> getLocations(DSLContext dsl, Integer[] locationIds, Integer[] locationGroupIds, Integer[] datasetIds)
	{
		// Optional conditions for germplasm restrictions
		Condition hasMarkedIds = !CollectionUtils.isEmpty(locationIds) ? LOCATIONS.ID.in(locationIds) : null;
		Condition hasGroupIds = !CollectionUtils.isEmpty(locationGroupIds) ? GROUPS.ID.in(locationGroupIds) : null;

		// Germplasm lookup
		Map<Integer, ViewTableClimateLocation> locations = new LinkedHashMap<>();

		// Select the germplasm

		List<Field<?>> fields = new ArrayList<>(List.of(VIEW_TABLE_LOCATIONS.fields()));

		if (hasGroupIds != null)
		{
			fields.add(DSL.select(DSL.jsonArrayAgg(GROUPS.ID))
						  .from(GROUPMEMBERS)
						  .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
						  .where(GROUPS.ID.in(locationGroupIds))
						  .and(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
						  .asField("groupIds"));
		}

		SelectJoinStep<?> gStep = dsl.select(fields)
									 .from(VIEW_TABLE_LOCATIONS);

		// Join more tables if groups are requested
		if (hasGroupIds != null)
			gStep.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
				 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID).and(GROUPS.GROUPTYPE_ID.eq(1)));

		// The overall condition, by default only limiting to the germplasm that has phenotypic data in those datasets
		Condition condition = DSL.exists(DSL.selectOne().from(CLIMATEDATA).where(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID)).and(CLIMATEDATA.DATASET_ID.in(datasetIds)).limit(1));

		// Add the optional conditions
		if (hasMarkedIds != null && hasGroupIds != null)
			condition = condition.and(hasMarkedIds.or(hasGroupIds));
		else if (hasMarkedIds != null)
			condition = condition.and(hasMarkedIds);
		else if (hasGroupIds != null)
			condition = condition.and(hasGroupIds);

		// Run the query and store germplasm mapping
		gStep.where(condition)
			 .forEach(gp -> {
				 ViewTableClimateLocation vgp = gp.into(ViewTableClimateLocation.class);
				 locations.put(vgp.getLocationId(), vgp);
			 });

		return locations;
	}

	/**
	 * Pulls the raw long-format rows and groups them by plot (the fields that define a "merge into one row" group).
	 */
	private Map<PlotKey, List<DataPoint>> fetchRawData(DSLContext context, Integer[] datasetIds, Collection<Integer> climateIds, Collection<Integer> locationIds)
	{
		Field<Integer> year = DSL.year(CLIMATEDATA.RECORDING_DATE).as("year");

		Result<?> raw = context.select(
									   VIEW_TABLE_LOCATIONS.LOCATION_ID,
									   VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE,
									   VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE,
									   VIEW_TABLE_LOCATIONS.LOCATION_ID,
									   CLIMATEDATA.DATASET_ID,
									   CLIMATEDATA.CLIMATE_ID,
									   CLIMATEDATA.CLIMATE_VALUE,
									   year,
									   CLIMATEDATA.RECORDING_DATE)
							   .from(CLIMATEDATA)
							   .leftJoin(VIEW_TABLE_LOCATIONS).on(VIEW_TABLE_LOCATIONS.LOCATION_ID.eq(CLIMATEDATA.LOCATION_ID))
							   .where(CollectionUtils.isEmpty(climateIds) ? DSL.trueCondition() : CLIMATEDATA.CLIMATE_ID.in(climateIds))
							   .and(CollectionUtils.isEmpty(locationIds) ? DSL.trueCondition() : CLIMATEDATA.LOCATION_ID.in(locationIds))
							   .and(CLIMATEDATA.DATASET_ID.in(datasetIds))
							   .fetch();

		Map<PlotKey, List<DataPoint>> byPlot = new LinkedHashMap<>();

		for (Record r : raw)
		{
			PlotKey key = new PlotKey(
					r.get(VIEW_TABLE_LOCATIONS.LOCATION_ID),
					r.get(VIEW_TABLE_LOCATIONS.LOCATION_LATITUDE, Double.class),
					r.get(VIEW_TABLE_LOCATIONS.LOCATION_LONGITUDE, Double.class),
					r.get(CLIMATEDATA.RECORDING_DATE),
					r.get(year)
			);

			DataPoint dp = new DataPoint(
					r.get(CLIMATEDATA.CLIMATE_ID),
					r.get(CLIMATEDATA.CLIMATE_VALUE),
					r.get(CLIMATEDATA.RECORDING_DATE),
					r.get(CLIMATEDATA.DATASET_ID));

			byPlot.computeIfAbsent(key, k -> new ArrayList<>()).add(dp);
		}

		return byPlot;
	}

	// ------------------------------------------------------------------
	// Aggregation
	// ------------------------------------------------------------------

	/**
	 * Averages every parseable numeric value contributing to this plot/variable combination.
	 */
	private String aggregateNumeric(List<DataPoint> points)
	{
		List<Double> values = new ArrayList<>();

		for (DataPoint dp : points)
		{
			if (dp.value() == null)
				continue;

			try
			{
				values.add(Double.parseDouble(dp.value().trim()));
			}
			catch (NumberFormatException ignored)
			{
				// skip anything that isn't actually numeric despite the variable's data type
			}
		}

		if (values.isEmpty())
			return null;

		return switch (aggregationMethod)
		{
			case MEAN -> Double.toString(values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
			case MEDIAN -> Double.toString(median(values));
		};
	}

	private Double median(List<Double> values)
	{
		List<Double> sorted = new ArrayList<>(values);
		Collections.sort(sorted);

		int size = sorted.size();
		int mid = size / 2;

		// even count -> average the two middle values; odd count -> take the middle one
		return (size % 2 == 0)
				? (sorted.get(mid - 1) + sorted.get(mid)) / 2.0
				: sorted.get(mid);
	}

	/**
	 * Takes the value recorded most recently. If several values share the latest recording_date
	 * (or none of them have a date at all), falls back to a majority vote; a genuine tie after
	 * that just takes the last one encountered so the result stays deterministic.
	 */
	private String aggregateNonNumeric(List<DataPoint> points)
	{
		List<DataPoint> nonNull = points.stream()
										.filter(p -> p.value() != null)
										.toList();

		if (nonNull.isEmpty())
			return null;

		boolean anyDated = nonNull.stream().anyMatch(p -> p.recordingDate() != null);

		if (anyDated)
		{
			Timestamp maxDate = nonNull.stream()
									   .map(DataPoint::recordingDate)
									   .filter(Objects::nonNull)
									   .max(Comparator.naturalOrder())
									   .orElseThrow();

			List<String> atMaxDate = nonNull.stream()
											.filter(p -> maxDate.equals(p.recordingDate()))
											.map(DataPoint::value)
											.collect(Collectors.toList());

			return majority(atMaxDate);
		}

		return majority(nonNull.stream().map(DataPoint::value).collect(Collectors.toList()));
	}

	private String majority(List<String> values)
	{
		if (values.size() == 1)
			return values.getFirst();

		Map<String, Long> counts = values.stream()
										 .collect(Collectors.groupingBy(v -> v, LinkedHashMap::new, Collectors.counting()));

		long maxCount = Collections.max(counts.values());

		List<String> winners = counts.entrySet().stream()
									 .filter(e -> e.getValue() == maxCount)
									 .map(Map.Entry::getKey)
									 .toList();

		return winners.size() == 1 ? winners.getFirst() : values.getLast();
	}

	// ------------------------------------------------------------------
	// Export helper
	// ------------------------------------------------------------------

	/**
	 * Flattens the matrix into a simple table: a header row of
	 * [germplasm_id, block, rep, trial_row, trial_column, variable1, variable2, ...]
	 * followed by one row per plot. Handy for dumping to CSV, a JTable, etc.
	 */
	public void writeTsv(ClimateMatrix matrix, Writer bw)
			throws IOException
	{
		// TODO add more germplasm fields/columns
		List<String> header = new ArrayList<>(List.of(
				"name",
				"dbId",
				"country",
				"dataset_ids",
				"date",
				"year",
				"groups",
				"latitude",
				"longitude"
		));
		header.addAll(climates.values().stream().map(t -> {
			String name = t.getClimateName();

			if (!StringUtils.isEmpty(t.getUnitAbbreviation()))
				name += " [" + t.getUnitAbbreviation() + "]";

			return name;
		}).toList());

		writeRow(bw, header);

		for (PlotKey key : matrix.plotKeys())
		{
			String datasetIds = gson.toJson(matrix.datasetIds().get(key).toArray(Integer[]::new));

			ViewTableClimateLocation gs = locations.get(key.locationId);
			String groupIds = "";
			if (!CollectionUtils.isEmpty(gs.getGroupIds()))
				groupIds = gson.toJson(gs.getGroupIds());

			List<String> row = new ArrayList<>(List.of(
					StringUtils.orEmpty(gs.getLocationName()),
					StringUtils.orEmpty(gs.getLocationId()),
					StringUtils.orEmpty(gs.getCountryName()),
					datasetIds,
					StringUtils.orEmpty(key.date()),
					StringUtils.orEmpty(key.year()),
					StringUtils.orEmpty(groupIds),
					StringUtils.orEmpty(key.latitude()),
					StringUtils.orEmpty(key.longitude())
			));

			Map<Integer, String> values = matrix.cells().get(key);
			for (Integer variableId : climates.keySet())
				row.add(StringUtils.orEmpty(values.get(variableId)));

			writeRow(bw, row);
		}
	}

	/**
	 * Writes a single tab-separated row, escaping any tabs/newlines that might be lurking in a phenotype value.
	 */
	private void writeRow(Writer writer, List<String> values)
			throws IOException
	{
		StringBuilder line = new StringBuilder();

		for (int i = 0; i < values.size(); i++)
		{
			if (i > 0)
				line.append('\t');

			Object value = values.get(i);
			if (value != null)
				line.append(sanitize(value.toString()));
		}

		writer.write(line.toString());
		writer.write(System.lineSeparator());
	}

	/**
	 * Guards against stray tabs/newlines in free-text phenotype values breaking the TSV structure.
	 */
	private String sanitize(String value)
	{
		return value.replace('\t', ' ').replace("\r\n", " ").replace('\n', ' ').replace('\r', ' ');
	}

	public static enum AggregationMethod
	{
		MEAN,
		MEDIAN
	}
}