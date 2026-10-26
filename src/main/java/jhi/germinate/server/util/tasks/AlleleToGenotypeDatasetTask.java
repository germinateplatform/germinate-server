package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.AttributesDatatype;
import jhi.germinate.server.database.codegen.tables.Datasetmembers;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.BinningConfig;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.AllelefreqExporter;
import jhi.germinate.server.util.hdf5.FJTabbedToHdf5Converter;
import lombok.*;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Attributedata.ATTRIBUTEDATA;
import static jhi.germinate.server.database.codegen.tables.Attributes.ATTRIBUTES;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

public class AlleleToGenotypeDatasetTask implements Runnable
{
	private static boolean running = false;

	@Override
	public void run()
	{
		if (running)
			return;

		running = true;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get any allele frequency datasets
			List<DatasetsRecord> alleleFrequencyDatasets = context.select(DATASETS.fields())
			                                                      .from(DATASETS)
			                                                      .where(DATASETS.DATASETTYPE_ID.eq(4))
			                                                      .fetchInto(DatasetsRecord.class);

			Logger.getLogger("").info("RUNNING AlleleToGenotypeDatasetTask for " + alleleFrequencyDatasets.size() + " datasets");

			// Get data folders for allele frequency and genotypic data
			File alleleFreqFolder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "data"), "allelefreq");
			File genotypeFolder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "data"), "genotypes");
			// Make sure the latter exists
			genotypeFolder.mkdirs();

			AttributesRecord binAttribute = context.selectFrom(ATTRIBUTES)
			                                       .where(ATTRIBUTES.NAME.eq("Allele frequency binning information"))
			                                       .and(ATTRIBUTES.TARGET_TABLE.eq("datasets"))
			                                       .fetchAnyInto(AttributesRecord.class);

			if (binAttribute == null)
			{
				binAttribute = context.newRecord(ATTRIBUTES);
				binAttribute.setName("Allele frequency binning information");
				binAttribute.setTargetTable("datasets");
				binAttribute.setDatatype(AttributesDatatype.text);
				binAttribute.store();
			}

			if (!alleleFreqFolder.exists())
				return;

			for (DatasetsRecord dataset : alleleFrequencyDatasets)
			{
				Integer originalId = dataset.getId();
				String originalName = dataset.getName();
				String originalDescription = dataset.getDescription();

				if (StringUtils.isEmpty(originalName))
					originalName = "";
				else
					originalName += " | ";

				if (StringUtils.isEmpty(originalDescription))
					originalDescription = "";
				else
					originalDescription += " | ";

				try
				{
					// Take a copy before we start. We'll use this copy for our second dataset.
					DatasetsRecord copy = dataset.copy();
					ConverterResult equal = handle(alleleFreqFolder, genotypeFolder, dataset, BinningConfig.DEFAULT);
					if (equal != null)
					{
						String binningData = String.join("\n", equal.getBins());
						// Update dataset information
						dataset.setDatasettypeId(1);
						dataset.setSourceFile(equal.getHdf5().getName());
						dataset.setName(originalName + "EQUAL BINNING");
						dataset.setDescription(originalDescription + binningData);
						// Save
						dataset.store();

						context.insertInto(ATTRIBUTEDATA)
						       .set(ATTRIBUTEDATA.ATTRIBUTE_ID, binAttribute.getId())
						       .set(ATTRIBUTEDATA.FOREIGN_ID, dataset.getId())
						       .set(ATTRIBUTEDATA.VALUE, binningData)
						       .execute();
					}

					ConverterResult auto = handle(alleleFreqFolder, genotypeFolder, copy, new BinningConfig("auto", 10, 0, 0.0F));
					if (auto != null)
					{
						String binningData = String.join("\n", auto.getBins());
						// Update dataset information
						copy.setDatasettypeId(1);
						copy.setSourceFile(auto.getHdf5().getName());
						copy.setName(originalName + "AUTO BINNING");
						copy.setDescription(originalDescription + binningData);
						// Save
						copy.store();

						Datasetmembers other = DATASETMEMBERS.as("other");
						context.insertInto(DATASETMEMBERS, DATASETMEMBERS.FOREIGN_ID, DATASETMEMBERS.DATASETMEMBERTYPE_ID, DATASETMEMBERS.DATASET_ID)
						       .select(context.select(
									   other.FOREIGN_ID,
									   other.DATASETMEMBERTYPE_ID,
									   DSL.val(copy.getId())
							   ).from(other).where(other.DATASET_ID.eq(originalId)))
						       .execute();

						context.insertInto(ATTRIBUTEDATA)
						       .set(ATTRIBUTEDATA.ATTRIBUTE_ID, binAttribute.getId())
						       .set(ATTRIBUTEDATA.FOREIGN_ID, copy.getId())
						       .set(ATTRIBUTEDATA.VALUE, binningData)
						       .execute();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// Remember to update user dataset permissions as we introduced a new dataset.
		AuthorizationFilter.refreshUserDatasetInfo(true);

		running = false;
	}

	private ConverterResult handle(File alleleFreqFolder, File genotypeFolder, DatasetsRecord dataset, BinningConfig config)
			throws SQLException, IOException
	{
		// Assign random uuid
		String uuid = UUID.randomUUID().toString();
		// Get configuration for allele frequency exporter
		File source = new File(alleleFreqFolder, dataset.getSourceFile());
		File binned = new File(genotypeFolder, uuid + "_" + dataset.getSourceFile() + "_" + config.getBinningMethod() + "_binned.txt");
		File unbinned = new File(genotypeFolder, uuid + "_" + dataset.getSourceFile() + "_unbinned.txt");
		File hdf5 = new File(genotypeFolder, uuid + "_" + dataset.getSourceFile() + "_" + config.getBinningMethod() + "_binned.hdf5");
		File identifiers = new File(genotypeFolder, uuid + "_" + dataset.getSourceFile() + "_identifiers.txt");

		if (!source.exists())
		{
			Logger.getLogger("").info("Allele frequency source file not found.");
			return null;
		}

		AllelefreqExporter.ImportResult result = new AllelefreqExporter()
				.setBinningConfig(config)
				.setDataset(dataset)
				.setSourceFile(source)
				.setProjectName(uuid)
				.setTabbedBinnedFile(binned)
				.setTabbedUnbinnedFile(unbinned)
				.setIdentifierFile(identifiers)
				.setHeaders("# \n")
				.run(false);

		Logger.getLogger("").info(result.toString());

		new FJTabbedToHdf5Converter(binned, hdf5).convertToHdf5();

		unbinned.delete();
		identifiers.delete();
		binned.delete();

		return new ConverterResult()
				.setHdf5(hdf5)
				.setBins(result.getBinInfo());
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class ConverterResult
	{
		private File         hdf5;
		private List<String> bins;
	}
}
