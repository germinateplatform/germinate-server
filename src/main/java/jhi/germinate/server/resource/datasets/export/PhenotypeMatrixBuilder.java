package jhi.germinate.server.resource.datasets.export;

import com.google.gson.Gson;
import jhi.germinate.resource.*;
import jhi.germinate.server.database.codegen.enums.ViewTableTraitsScaleDatatype;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;
import static jhi.germinate.server.database.codegen.tables.Treatments.TREATMENTS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

/**
 * Pulls long-format phenotype data (germplasm, trait, value) out of
 * PHENOTYPEDATA / TRIALSETUP and pivots it into a matrix of
 * germplasm (+ plot position) x trait.
 * <p>
 * Rows are merged together only when block, rep, trial_row and trial_column
 * all match - i.e. they come from the same physical plot. Numeric traits are
 * averaged across whatever contributes to that plot; everything else takes
 * the most recently recorded value, falling back to a majority vote.
 */
public class PhenotypeMatrixBuilder
{
	private Gson                                  gson;
	private Map<Integer, ViewTableTraits>         traits;
	private Map<Integer, ViewTableTrialGermplasm> germplasm;
	private AggregationMethod                     aggregationMethod = AggregationMethod.MEDIAN;

	/**
	 * Uniquely identifies a single plot instance. Rows are only ever merged within the same key.
	 */
	public record PlotKey(int germplasmId, String block, String rep, String treatment, Short trialRow,
	                      Short trialColumn, Integer locationId, Double latitude, Double longitude, Integer year)
	{
	}

	/**
	 * One raw (variable, value, date) triple read from phenotypedata for a given plot.
	 */
	private record DataPoint(int traitId, String value, Timestamp recordingDate, int datasetId)
	{
	}

	/**
	 * Final pivoted result.
	 */
	public record PhenotypeMatrix(List<PlotKey> plotKeys,
	                              Map<PlotKey, Map<Integer, String>> cells,
	                              Map<PlotKey, Set<Integer>> datasetIds)
	{
	}

	public PhenotypeMatrixBuilder()
	{
		gson = new Gson();
	}

	public PhenotypeMatrixBuilder(AggregationMethod aggregationMethod)
	{
		this();
		this.aggregationMethod = aggregationMethod;
	}

	/**
	 * @param dsl     jOOQ context
	 * @param request the export request
	 */
	public PhenotypeMatrix buildMatrix(DSLContext dsl, TrialsExportDatasetRequest request)
	{
		// Get requested items
		traits = getTraits(dsl, request.getTraitIds());
		germplasm = getGermplasm(dsl, request.getGermplasmIds(), request.getGermplasmGroupIds(), request.getDatasetIds());

		Set<Integer> numericTraitIds = traits.keySet().stream().filter(traitId -> traits.get(traitId).getScaleDatatype() == ViewTableTraitsScaleDatatype.numeric).collect(Collectors.toSet());

		// Get the actual data
		Map<PlotKey, List<DataPoint>> byPlot = fetchRawData(dsl, request.getDatasetIds(), traits.keySet(), germplasm.keySet());

		List<PlotKey> plotKeys = new ArrayList<>(byPlot.keySet());
		Map<PlotKey, Map<Integer, String>> cells = new LinkedHashMap<>();
		Map<PlotKey, Set<Integer>> datasetIdsByPlot = new LinkedHashMap<>();

		for (Map.Entry<PlotKey, List<DataPoint>> entry : byPlot.entrySet())
		{
			Map<Integer, List<DataPoint>> byTrait = entry.getValue().stream()
			                                             .collect(Collectors.groupingBy(DataPoint::traitId, LinkedHashMap::new, Collectors.toList()));

			Map<Integer, String> traitValues = new LinkedHashMap<>();
			for (Map.Entry<Integer, List<DataPoint>> traitEntry : byTrait.entrySet())
			{
				int traitId = traitEntry.getKey();
				List<DataPoint> points = traitEntry.getValue();

				String aggregated = numericTraitIds.contains(traitId)
						? aggregateNumeric(points)
						: aggregateNonNumeric(points);

				traitValues.put(traitId, aggregated);
			}

			cells.put(entry.getKey(), traitValues);

			Set<Integer> dsIds = entry.getValue().stream()
			                          .map(DataPoint::datasetId)
			                          .collect(Collectors.toCollection(LinkedHashSet::new));
			datasetIdsByPlot.put(entry.getKey(), dsIds);
		}

		return new PhenotypeMatrix(plotKeys, cells, datasetIdsByPlot);
	}

	// ------------------------------------------------------------------
	// Data access
	// ------------------------------------------------------------------
	private Map<Integer, ViewTableTraits> getTraits(DSLContext dsl, Integer[] traitIds)
	{
		return dsl.select()
		          .from(VIEW_TABLE_TRAITS)
		          .where(VIEW_TABLE_TRAITS.TRAIT_ID.in(traitIds))
		          .fetchMap(VIEW_TABLE_TRAITS.TRAIT_ID, ViewTableTraits.class);
	}

	private Map<Integer, ViewTableTrialGermplasm> getGermplasm(DSLContext dsl, Integer[] germplasmIds, Integer[] germplasmGroupIds, Integer[] datasetIds)
	{
		// Optional conditions for germplasm restrictions
		Condition hasMarkedIds = !CollectionUtils.isEmpty(germplasmIds) ? GERMINATEBASE.ID.in(germplasmIds) : null;
		Condition hasGroupIds = !CollectionUtils.isEmpty(germplasmGroupIds) ? GROUPS.ID.in(germplasmGroupIds) : null;

		// Germplasm lookup
		Map<Integer, ViewTableTrialGermplasm> germplasm = new LinkedHashMap<>();

		// Select the germplasm
		jhi.germinate.server.database.codegen.tables.Germinatebase g = GERMINATEBASE.as("g");
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				GERMINATEBASE.ID.as("germplasmId"),
				GERMINATEBASE.NAME.as("germplasmName"),
				GERMINATEBASE.DISPLAY_NAME.as("germplasmDisplayName"),
				GERMINATEBASE.GENERAL_IDENTIFIER.as("germplasmGid"),
				TAXONOMIES.GENUS.as("genus"),
				TAXONOMIES.SPECIES.as("species"),
				TAXONOMIES.SUBTAXA.as("subtaxa"),
				MCPD.PUID.as("puid"),
				g.NAME.as("entityParentName"),
				g.GENERAL_IDENTIFIER.as("entityParentGeneralIdentifier")
		));

		if (hasGroupIds != null)
		{
			fields.add(DSL.select(DSL.jsonArrayAgg(GROUPS.ID))
			              .from(GROUPMEMBERS)
			              .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
			              .where(GROUPS.ID.in(germplasmGroupIds))
			              .and(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			              .asField("groupIds"));
		}

		SelectOnConditionStep<?> gStep = dsl.select(fields)
		                                    .from(GERMINATEBASE)
		                                    .leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
		                                    .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
		                                    .leftJoin(MCPD).on(MCPD.GERMINATEBASE_ID.eq(GERMINATEBASE.ID));

		// Join more tables if groups are requested
		if (hasGroupIds != null)
			gStep.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			     .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID).and(GROUPS.GROUPTYPE_ID.eq(3)));

		// The overall condition, by default only limiting to the germplasm that has phenotypic data in those datasets
		Condition condition = DSL.exists(DSL.selectOne().from(PHENOTYPEDATA).leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)).where(TRIALSETUP.GERMINATEBASE_ID.eq(GERMINATEBASE.ID)).and(TRIALSETUP.DATASET_ID.in(datasetIds)).limit(1));

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
				 ViewTableTrialGermplasm vgp = gp.into(ViewTableTrialGermplasm.class);
				 germplasm.put(vgp.getGermplasmId(), vgp);
			 });

		return germplasm;
	}

	/**
	 * Pulls the raw long-format rows and groups them by plot (the fields that define a "merge into one row" group).
	 */
	private Map<PlotKey, List<DataPoint>> fetchRawData(DSLContext context, Integer[] datasetIds, Collection<Integer> traitIds, Collection<Integer> germplasmIds)
	{
		Field<Integer> year = DSL.year(PHENOTYPEDATA.RECORDING_DATE).as("year");

		Result<?> raw = context.select(
									   TRIALSETUP.GERMINATEBASE_ID,
									   TRIALSETUP.BLOCK,
									   TRIALSETUP.REP,
									   TRIALSETUP.TRIAL_ROW,
									   TRIALSETUP.TRIAL_COLUMN,
									   TRIALSETUP.DATASET_ID,
									   PHENOTYPEDATA.VARIABLE_ID,
									   PHENOTYPEDATA.PHENOTYPE_VALUE,
									   TRIALSETUP.LOCATION_ID,
									   TRIALSETUP.LATITUDE,
									   TRIALSETUP.LONGITUDE,
									   TREATMENTS.NAME,
									   year,
									   PHENOTYPEDATA.RECORDING_DATE)
		                       .from(PHENOTYPEDATA)
		                       .leftJoin(TRIALSETUP).on(PHENOTYPEDATA.TRIALSETUP_ID.eq(TRIALSETUP.ID))
		                       .leftJoin(TREATMENTS).on(TREATMENTS.ID.eq(TRIALSETUP.TREATMENT_ID))
		                       .where(CollectionUtils.isEmpty(traitIds) ? DSL.trueCondition() : PHENOTYPEDATA.VARIABLE_ID.in(traitIds))
		                       .and(CollectionUtils.isEmpty(germplasmIds) ? DSL.trueCondition() : TRIALSETUP.GERMINATEBASE_ID.in(germplasmIds))
		                       .and(TRIALSETUP.DATASET_ID.in(datasetIds))
		                       .fetch();

		Map<PlotKey, List<DataPoint>> byPlot = new LinkedHashMap<>();

		for (Record r : raw)
		{
			PlotKey key = new PlotKey(
					r.get(TRIALSETUP.GERMINATEBASE_ID),
					r.get(TRIALSETUP.BLOCK),
					r.get(TRIALSETUP.REP),
					r.get(TREATMENTS.NAME),
					r.get(TRIALSETUP.TRIAL_ROW),
					r.get(TRIALSETUP.TRIAL_COLUMN),
					r.get(TRIALSETUP.LOCATION_ID),
					r.get(TRIALSETUP.LATITUDE, Double.class),
					r.get(TRIALSETUP.LONGITUDE, Double.class),
					r.get(year));

			DataPoint dp = new DataPoint(
					r.get(PHENOTYPEDATA.VARIABLE_ID),
					r.get(PHENOTYPEDATA.PHENOTYPE_VALUE),
					r.get(PHENOTYPEDATA.RECORDING_DATE),
					r.get(TRIALSETUP.DATASET_ID));

			byPlot.computeIfAbsent(key, k -> new ArrayList<>()).add(dp);
		}

		return byPlot;
	}

	// ------------------------------------------------------------------
	// Aggregation
	// ------------------------------------------------------------------

	/**
	 * Averages every parseable numeric value contributing to this plot/trait combination.
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
				// skip anything that isn't actually numeric despite the trait's data type
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
	 * [germplasm_id, block, rep, trial_row, trial_column, trait1, trait2, ...]
	 * followed by one row per plot. Handy for dumping to CSV, a JTable, etc.
	 */
	public void writeTsv(PhenotypeMatrix matrix, Writer bw)
			throws IOException
	{
		// TODO add more germplasm fields/columns
		List<String> header = new ArrayList<>(List.of(
				"name",
				"dbId",
				"puid",
				"general_identifier",
				"taxonomy",
				"entity_parent_name",
				"entity_parent_general_identifier",
				"dataset_ids",
				"year",
				"groups",
				"location",
				"latitude",
				"longitude",
				"treatment",
				"rep",
				"block",
				"trial_row",
				"trial_column"
		));
		header.addAll(traits.values().stream().map(t -> {
			String name = t.getTraitName();

			if (!StringUtils.isEmpty(t.getScaleUnit()))
				name += " [" + t.getScaleUnit() + "]";

			return name;
		}).toList());

		writeRow(bw, header);

		for (PlotKey key : matrix.plotKeys())
		{
			String datasetIds = gson.toJson(matrix.datasetIds().get(key).toArray(Integer[]::new));

			ViewTableTrialGermplasm gs = germplasm.get(key.germplasmId);
			String groupIds = "";
			if (!CollectionUtils.isEmpty(gs.getGroupIds()))
				groupIds = gson.toJson(gs.getGroupIds());

			List<String> row = new ArrayList<>(List.of(
					StringUtils.orEmpty(gs.getGermplasmDisplayName()),
					StringUtils.orEmpty(gs.getGermplasmId()),
					StringUtils.orEmpty(gs.getGermplasmPuid()),
					StringUtils.orEmpty(gs.getGermplasmGid()),
					StringUtils.join(" ", gs.getGenus(), gs.getSpecies(), gs.getSubtaxa()),
					StringUtils.orEmpty(gs.getEntityParentName()),
					StringUtils.orEmpty(gs.getEntityParentGeneralIdentifier()),
					datasetIds,
					StringUtils.orEmpty(key.year()),
					StringUtils.orEmpty(groupIds),
					StringUtils.orEmpty(key.locationId()),
					StringUtils.orEmpty(key.latitude()),
					StringUtils.orEmpty(key.longitude()),
					StringUtils.orEmpty(key.treatment()),
					StringUtils.orEmpty(key.rep()),
					StringUtils.orEmpty(key.block()),
					StringUtils.orEmpty(key.trialRow()),
					StringUtils.orEmpty(key.trialColumn())
			));

			Map<Integer, String> values = matrix.cells().get(key);
			for (Integer traitId : traits.keySet())
				row.add(StringUtils.orEmpty(values.get(traitId)));

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
		MEDIAN;
	}
}