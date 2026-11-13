package jhi.germinate.server.resource.datasets;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

public class DatasetSimilarityIndex
{
	// Represents the cached features of a single dataset in memory
	public record DatasetFeatures(
			int datasetId,
			String datasetType, // "TRIAL", "GENOTYPE", "CLIMATE", etc.
			Set<Integer> germplasmIds,
			Set<Integer> traitIds,
			Set<Integer> markerIds,
			Set<Integer> locationIds,
			Set<Integer> climateVarIds
	)
	{
	}

	// DTO for API Request
	public record SimilarityRequest(List<Integer> datasetIds)
	{
	}

	// DTO for API Response
	public record SimilarityResult(
			int datasetId,
			double similarityScore
	)
	{
	}

	// Default weights assigned to each domain type
	private static final Map<String, Double> DEFAULT_WEIGHTS = Map.of(
			"germplasm", 0.30,
			"traits", 0.30,
			"markers", 0.20,
			"locations", 0.10,
			"climate", 0.10
	);

	// Primary registry storing all dataset features in memory
	private static final ConcurrentHashMap<Integer, DatasetFeatures> datasetRegistry = new ConcurrentHashMap<>();

	// Inverted index mapping "FEATURE_TYPE_ID" -> Set of Dataset IDs containing it
	// Example key: "MARKER_48921" -> [101, 105, 204]
	private static final ConcurrentHashMap<String, Set<Integer>> invertedIndex = new ConcurrentHashMap<>();

	// ReadWriteLock ensures thread safety during dynamic additions/deletions without blocking concurrent reads
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();

	public static void print()
	{
		Logger.getLogger("").info("DatasetSimilarityIndex registry size: " + datasetRegistry.size());
		Logger.getLogger("").info("DatasetSimilarityIndex inverted index size: " + invertedIndex.size());
	}

	/**
	 * Registers or updates a dataset in memory (used at startup and when datasets are added/updated).
	 */
	public static void upsertDataset(DatasetFeatures dataset)
	{
		lock.writeLock().lock();
		try
		{
			// Remove old index references if updating an existing dataset
			removeDatasetFromIndex(dataset.datasetId());

			datasetRegistry.put(dataset.datasetId(), dataset);

			addToIndex("GERMPLASM", dataset.datasetId(), dataset.germplasmIds());
			addToIndex("TRAIT", dataset.datasetId(), dataset.traitIds());
			addToIndex("MARKER", dataset.datasetId(), dataset.markerIds());
			addToIndex("LOCATION", dataset.datasetId(), dataset.locationIds());
			addToIndex("CLIMATE", dataset.datasetId(), dataset.climateVarIds());
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Removes a dataset from memory (when deleted).
	 */
	public static void removeDataset(int datasetId)
	{
		lock.writeLock().lock();
		try
		{
			datasetRegistry.remove(datasetId);
			removeDatasetFromIndex(datasetId);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Main API logic: Finds similar datasets given a list of input IDs and weights.
	 */
	public static List<SimilarityResult> findSimilarDatasets(List<Integer> inputDatasetIds, String targetType, double threshold)
	{
		lock.readLock().lock();
		try
		{
			List<DatasetFeatures> sources = inputDatasetIds.stream()
														   .map(datasetRegistry::get)
														   .filter(Objects::nonNull)
														   .toList();

			if (sources.isEmpty())
				return Collections.emptyList();

			// 1. Candidate Generation via Inverted Index
			Set<Integer> candidateIds = new HashSet<>();
			for (DatasetFeatures src : sources)
				candidateIds.addAll(getCandidatesForDataset(src));
			inputDatasetIds.forEach(candidateIds::remove);

			// 2. Optional Type Filtering
			if (targetType != null && !targetType.isBlank()) {
				candidateIds.removeIf(candidateId -> {
					DatasetFeatures candidate = datasetRegistry.get(candidateId);
					// Remove if candidate doesn't exist or its type doesn't match the requested target type
					return candidate == null || !candidate.datasetType().equalsIgnoreCase(targetType);
				});
			}

			// 3. Score calculation across all candidates in parallel
			Map<Integer, Double> scores = new HashMap<>();
			for (Integer candidateId : candidateIds)
			{
				DatasetFeatures candidate = datasetRegistry.get(candidateId);
				if (candidate == null) continue;

				double averageScore = sources.stream()
											 .mapToDouble(src -> calculateAutomaticMultiFactorSimilarity(src, candidate))
											 .average()
											 .orElse(0.0);

				scores.put(candidateId, averageScore);
			}

			// 4. Sort descending and return top N
			return scores.entrySet().stream()
						 .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
					.filter(e -> e.getValue() >= threshold)
						 .map(entry -> new SimilarityResult(entry.getKey(), entry.getValue()))
						 .toList();

		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	private static double calculateAutomaticMultiFactorSimilarity(DatasetFeatures d1, DatasetFeatures d2)
	{
		double weightedSum = 0.0;
		double totalActiveWeight = 0.0;

		// Evaluate each factor independently
		weightedSum += evaluateFactor(d1.germplasmIds(), d2.germplasmIds(), DEFAULT_WEIGHTS.get("germplasm"));
		totalActiveWeight += getActiveWeight(d1.germplasmIds(), d2.germplasmIds(), DEFAULT_WEIGHTS.get("germplasm"));

		weightedSum += evaluateFactor(d1.traitIds(), d2.traitIds(), DEFAULT_WEIGHTS.get("traits"));
		totalActiveWeight += getActiveWeight(d1.traitIds(), d2.traitIds(), DEFAULT_WEIGHTS.get("traits"));

		weightedSum += evaluateFactor(d1.markerIds(), d2.markerIds(), DEFAULT_WEIGHTS.get("markers"));
		totalActiveWeight += getActiveWeight(d1.markerIds(), d2.markerIds(), DEFAULT_WEIGHTS.get("markers"));

		weightedSum += evaluateFactor(d1.locationIds(), d2.locationIds(), DEFAULT_WEIGHTS.get("locations"));
		totalActiveWeight += getActiveWeight(d1.locationIds(), d2.locationIds(), DEFAULT_WEIGHTS.get("locations"));

		weightedSum += evaluateFactor(d1.climateVarIds(), d2.climateVarIds(), DEFAULT_WEIGHTS.get("climate"));
		totalActiveWeight += getActiveWeight(d1.climateVarIds(), d2.climateVarIds(), DEFAULT_WEIGHTS.get("climate"));

		// If both datasets share zero common domain types, similarity is 0
		if (totalActiveWeight == 0.0)
		{
			return 0.0;
		}

		// Normalize score based *only* on the weights of the factors that were actually active
		return weightedSum / totalActiveWeight;
	}

	private static double evaluateFactor(Set<Integer> set1, Set<Integer> set2, double weight)
	{
		if (isInvalid(set1) || isInvalid(set2))
		{
			return 0.0; // Skip if either dataset lacks this data type
		}
		return weight * jaccardIndex(set1, set2);
	}

	private static double getActiveWeight(Set<Integer> set1, Set<Integer> set2, double weight)
	{
		if (isInvalid(set1) || isInvalid(set2))
		{
			return 0.0; // Do not contribute to the denominator if factor is missing in action
		}
		return weight;
	}

	private static boolean isInvalid(Set<Integer> set)
	{
		return set == null || set.isEmpty();
	}

	// --- Helper Methods ---

	private static void addToIndex(String prefix, int datasetId, Set<Integer> featureIds)
	{
		if (featureIds == null) return;
		for (Integer id : featureIds)
		{
			String indexKey = prefix + "_" + id;
			invertedIndex.computeIfAbsent(indexKey, k -> ConcurrentHashMap.newKeySet()).add(datasetId);
		}
	}

	private static void removeDatasetFromIndex(int datasetId)
	{
		// Quick clean-up pass across inverted index entries containing this dataset ID
		invertedIndex.values().forEach(datasetSet -> datasetSet.remove(datasetId));
	}

	private static Set<Integer> getCandidatesForDataset(DatasetFeatures ds)
	{
		Set<Integer> candidates = new HashSet<>();
		collectMatches(invertedIndex, "GERMPLASM", ds.germplasmIds(), candidates);
		collectMatches(invertedIndex, "TRAIT", ds.traitIds(), candidates);
		collectMatches(invertedIndex, "MARKER", ds.markerIds(), candidates);
		collectMatches(invertedIndex, "LOCATION", ds.locationIds(), candidates);
		collectMatches(invertedIndex, "CLIMATE", ds.climateVarIds(), candidates);
		return candidates;
	}

	private static void collectMatches(Map<String, Set<Integer>> index, String prefix, Set<Integer> ids, Set<Integer> destination)
	{
		if (ids == null) return;
		for (Integer id : ids)
		{
			Set<Integer> matches = index.get(prefix + "_" + id);
			if (matches != null)
			{
				destination.addAll(matches);
			}
		}
	}

	private static double jaccardIndex(Set<Integer> set1, Set<Integer> set2)
	{
		Set<Integer> intersection = new HashSet<>(set1);
		intersection.retainAll(set2);
		if (intersection.isEmpty()) return 0.0;

		int unionSize = set1.size() + set2.size() - intersection.size();
		return (double) intersection.size() / unionSize;
	}
}
