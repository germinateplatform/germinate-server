package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;
import jhi.germinate.server.resource.datasets.DatasetSimilarityIndex;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.Datasetlocations.DATASETLOCATIONS;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.PEDIGREES;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;

public class DatasetSimilarityTask implements Runnable
{
	@Override
	public void run()
	{
		Logger.getLogger("").log(Level.INFO, "Running DatasetSimilarityTask");
		long start = System.currentTimeMillis();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// 1. Bulk fetch using standard Java Stream collectors to guarantee type safety
			Map<Integer, Set<Integer>> trialGermplasmMap = context.select(TRIALSETUP.DATASET_ID, TRIALSETUP.GERMINATEBASE_ID)
																  .from(TRIALSETUP)
																  .fetch()
																  .stream()
																  .collect(Collectors.groupingBy(
																		  r -> r.get(TRIALSETUP.DATASET_ID),
																		  Collectors.mapping(r -> r.get(TRIALSETUP.GERMINATEBASE_ID), Collectors.toSet())
																  ));

			Map<Integer, Set<Integer>> trialVariableMap = context.select(TRIALSETUP.DATASET_ID, PHENOTYPEDATA.VARIABLE_ID)
																 .from(PHENOTYPEDATA)
																 .join(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
																 .fetch()
																 .stream()
																 .collect(Collectors.groupingBy(
																		 r -> r.get(TRIALSETUP.DATASET_ID),
																		 Collectors.mapping(r -> r.get(PHENOTYPEDATA.VARIABLE_ID), Collectors.toSet())
																 ));

			Map<Integer, Set<Integer>> genoGermplasmMap = context.select(DATASETMEMBERS.DATASET_ID, DATASETMEMBERS.FOREIGN_ID)
																 .from(DATASETMEMBERS)
																 .where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
																 .fetch()
																 .stream()
																 .collect(Collectors.groupingBy(
																		 r -> r.get(DATASETMEMBERS.DATASET_ID),
																		 Collectors.mapping(r -> r.get(DATASETMEMBERS.FOREIGN_ID), Collectors.toSet())
																 ));

			Map<Integer, Set<Integer>> genoMarkerMap = context.select(DATASETMEMBERS.DATASET_ID, DATASETMEMBERS.FOREIGN_ID)
															  .from(DATASETMEMBERS)
															  .where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
															  .fetch()
															  .stream()
															  .collect(Collectors.groupingBy(
																	  r -> r.get(DATASETMEMBERS.DATASET_ID),
																	  Collectors.mapping(r -> r.get(DATASETMEMBERS.FOREIGN_ID), Collectors.toSet())
															  ));

			Map<Integer, Set<Integer>> climateLocationMap = context.select(CLIMATEDATA.DATASET_ID, CLIMATEDATA.LOCATION_ID)
																   .from(CLIMATEDATA)
																   .fetch()
																   .stream()
																   .collect(Collectors.groupingBy(
																		   r -> r.get(CLIMATEDATA.DATASET_ID),
																		   Collectors.mapping(r -> r.get(CLIMATEDATA.LOCATION_ID), Collectors.toSet())
																   ));

			Map<Integer, Set<Integer>> climateVarMap = context.select(CLIMATEDATA.DATASET_ID, CLIMATEDATA.CLIMATE_ID)
															  .from(CLIMATEDATA)
															  .fetch()
															  .stream()
															  .collect(Collectors.groupingBy(
																	  r -> r.get(CLIMATEDATA.DATASET_ID),
																	  Collectors.mapping(r -> r.get(CLIMATEDATA.CLIMATE_ID), Collectors.toSet())
															  ));

			Map<Integer, Set<Integer>> datasetLocationMap = context.select(DATASETLOCATIONS.DATASET_ID, DATASETLOCATIONS.LOCATION_ID)
																   .from(DATASETLOCATIONS)
																   .fetch()
																   .stream()
																   .collect(Collectors.groupingBy(
																		   r -> r.get(DATASETLOCATIONS.DATASET_ID),
																		   Collectors.mapping(r -> r.get(DATASETLOCATIONS.LOCATION_ID), Collectors.toSet())
																   ));

			Map<Integer, Set<Integer>> pedigreeGermplasmMap = context.select(PEDIGREES.DATASET_ID, PEDIGREES.GERMINATEBASE_ID)
																	 .from(PEDIGREES)
																	 .fetch()
																	 .stream()
																	 .collect(Collectors.groupingBy(
																			 r -> r.get(PEDIGREES.DATASET_ID),
																			 Collectors.mapping(r -> r.get(PEDIGREES.GERMINATEBASE_ID), Collectors.toSet())
																	 ));

			Map<Integer, Set<Integer>> pedigreeParentMap = context.select(PEDIGREES.DATASET_ID, PEDIGREES.PARENT_ID)
																  .from(PEDIGREES)
																  .fetch()
																  .stream()
																  .collect(Collectors.groupingBy(
																		  r -> r.get(PEDIGREES.DATASET_ID),
																		  Collectors.mapping(r -> r.get(PEDIGREES.PARENT_ID), Collectors.toSet())
																  ));

			Map<Integer, Set<Integer>> pedigreeDefMap = context.select(PEDIGREEDEFINITIONS.DATASET_ID, PEDIGREEDEFINITIONS.GERMINATEBASE_ID)
															   .from(PEDIGREEDEFINITIONS)
															   .fetch()
															   .stream()
															   .collect(Collectors.groupingBy(
																	   r -> r.get(PEDIGREEDEFINITIONS.DATASET_ID),
																	   Collectors.mapping(r -> r.get(PEDIGREEDEFINITIONS.GERMINATEBASE_ID), Collectors.toSet())
															   ));

			// 2. Loop through datasets and assemble objects in memory
			context.selectFrom(VIEW_TABLE_DATASETS)
				   .forEach(d -> {
					   Integer id = d.getDatasetId();
					   String type = d.getDatasetType();

					   Set<Integer> germplasm = new HashSet<>();
					   Set<Integer> variables = new HashSet<>();
					   Set<Integer> markers = new HashSet<>();
					   Set<Integer> locations = datasetLocationMap.getOrDefault(id, Collections.emptySet());
					   Set<Integer> climates = new HashSet<>();

					   switch (type)
					   {
						   case "trials":
							   germplasm = trialGermplasmMap.getOrDefault(id, Collections.emptySet());
							   variables = trialVariableMap.getOrDefault(id, Collections.emptySet());
							   break;
						   case "genotype":
							   germplasm = genoGermplasmMap.getOrDefault(id, Collections.emptySet());
							   markers = genoMarkerMap.getOrDefault(id, Collections.emptySet());
							   break;
						   case "climate":
							   locations = climateLocationMap.getOrDefault(id, Collections.emptySet());
							   climates = climateVarMap.getOrDefault(id, Collections.emptySet());
							   break;
						   case "pedigree":
							   germplasm = new HashSet<>(pedigreeGermplasmMap.getOrDefault(id, Collections.emptySet()));
							   germplasm.addAll(pedigreeParentMap.getOrDefault(id, Collections.emptySet()));
							   germplasm.addAll(pedigreeDefMap.getOrDefault(id, Collections.emptySet()));
							   break;
					   }

					   DatasetSimilarityIndex.DatasetFeatures datasetFeatures = new DatasetSimilarityIndex.DatasetFeatures(
							   id,
							   type,
							   germplasm,
							   variables,
							   markers,
							   locations,
							   climates
					   );

					   DatasetSimilarityIndex.upsertDataset(datasetFeatures);
				   });

			Logger.getLogger("").log(Level.INFO, "DatasetSimilarityTask complete in " + (System.currentTimeMillis() - start) + "ms");

			DatasetSimilarityIndex.print();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getMessage());
		}
	}
}
