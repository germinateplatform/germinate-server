package jhi.germinate.server.util.async;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.binning.*;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.database.pojo.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;

/**
 * @author Sebastian Raubach
 */
public class AllelefreqExporter
{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private File folder;
	private File sourceFile;
	private File mapFile;
	private File tabbedBinnedFile;
	private File tabbedUnbinnedFile;
	private File germplasmFile;
	private File flapjackProjectFile;
	private File identifierFile;
	private File zipFile;

	private Set<String>   germplasm;
	private Set<String>   markers;
	private BinningConfig binningConfig;
	private String        headers = "";
	private String        projectName;

	private DatasetExportJobs exportJob;
	private Datasets          dataset;

	public AllelefreqExporter()
	{
	}

	public static void main(String[] args)
		throws IOException
	{
		AllelefreqExporter exporter = new AllelefreqExporter();
		Database.init(args[0], args[1], args[2], args[3], args[4], false);
		Integer jobId = Integer.parseInt(args[5]);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			exporter.exportJob = context.selectFrom(DATASET_EXPORT_JOBS).where(DATASET_EXPORT_JOBS.ID.eq(jobId)).fetchAnyInto(DatasetExportJobs.class);
			exporter.dataset = context.selectFrom(DATASETS).where(DATASETS.ID.eq(exporter.exportJob.getDatasetIds()[0])).fetchAnyInto(Datasets.class);

			exporter.sourceFile = new File(new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "data"), "allelefreq"), exporter.dataset.getSourceFile());

			exporter.folder = new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "async"), exporter.exportJob.getUuid());
			exporter.projectName = "dataset-" + exporter.dataset.getId();

			if (exporter.exportJob.getJobConfig().getSubsetId() != null)
				exporter.projectName += "-map-" + exporter.exportJob.getJobConfig().getSubsetId();

			List<AdditionalExportFormat> formats = Arrays.asList(exporter.exportJob.getJobConfig().getFileTypes());

			exporter.tabbedBinnedFile = new File(exporter.folder, exporter.projectName + ".txt");
			exporter.tabbedUnbinnedFile = new File(exporter.folder, exporter.projectName + "-unbinned.txt");
			exporter.zipFile = new File(exporter.folder, exporter.projectName + "-" + SDF.format(new Date()) + ".zip");

			exporter.mapFile = new File(exporter.folder, exporter.projectName + ".map");
			exporter.identifierFile = new File(exporter.folder, exporter.projectName + ".identifiers");

			exporter.binningConfig = exporter.exportJob.getJobConfig().getBinningConfig();

			if (formats.contains(AdditionalExportFormat.flapjack))
				exporter.flapjackProjectFile = new File(exporter.folder, exporter.projectName + ".flapjack");

			exporter.run();

			DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS).where(DATASET_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			record.setStatus(DatasetExportJobsStatus.completed);
			record.setResultSize(exporter.zipFile.length());
			record.store(DATASET_EXPORT_JOBS.STATUS, DATASET_EXPORT_JOBS.RESULT_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS).where(DATASET_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
				record.setStatus(DatasetExportJobsStatus.failed);
				record.store(DATASET_EXPORT_JOBS.STATUS);
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
				Logger.getLogger("").severe(ee.getMessage());
			}
		}
	}

	private void init()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Write the map to a file
			if (exportJob.getJobConfig().getSubsetId() != null)
			{
				mapFile = new File(folder, projectName + ".map");
				SelectConditionStep<Record3<String, String, Double>> query = context.select(MARKERS.MARKER_NAME, MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START)
																					.from(MAPDEFINITIONS)
																					.leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
																					.leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MARKERS.ID).and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1)))
																					.where(DATASETMEMBERS.DATASET_ID.eq(exportJob.getDatasetIds()[0]))
																					.and(MAPDEFINITIONS.MAP_ID.eq(exportJob.getJobConfig().getSubsetId()));

				try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapFile), StandardCharsets.UTF_8)));
					 Cursor<? extends Record> cursor = query.fetchLazy())
				{
					bw.write("# fjFile = MAP" + ResourceUtils.CRLF);

					ResourceUtils.exportToFileStreamed(bw, cursor, false, null);
				}
			}

			// Get the germplasm
			if (exportJob.getJobConfig().getyGroupIds() != null || exportJob.getJobConfig().getyIds() != null)
			{
				Set<String> result = new LinkedHashSet<>();

				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getyIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .where(GERMINATEBASE.ID.in(exportJob.getJobConfig().getyIds()))
										 .fetchInto(String.class));
				}
				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getyGroupIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
										 .where(GROUPMEMBERS.GROUP_ID.in(exportJob.getJobConfig().getyGroupIds()))
										 .fetchInto(String.class));
				}

				this.germplasm = result;
			}

			// Get the markers
			if (exportJob.getJobConfig().getxGroupIds() != null || exportJob.getJobConfig().getxIds() != null)
			{
				Set<String> result = new LinkedHashSet<>();
				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getxIds()))
				{
					result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
										 .from(MARKERS)
										 .where(MARKERS.ID.in(exportJob.getJobConfig().getxIds()))
										 .fetchInto(String.class));
				}

				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getxGroupIds()))
				{
					result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
										 .from(MARKERS)
										 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(MARKERS.ID))
										 .where(GROUPMEMBERS.GROUP_ID.in(exportJob.getJobConfig().getxGroupIds()))
										 .fetchInto(String.class));
				}

				if (exportJob.getJobConfig().getSubsetId() != null)
				{
					// Only keep those that are actually on the map
					result.retainAll(context.selectDistinct(MARKERS.MARKER_NAME)
											.from(MARKERS)
											.leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID))
											.where(MAPDEFINITIONS.MAP_ID.eq(exportJob.getJobConfig().getSubsetId()))
											.fetchInto(String.class));
				}

				markers = result;
			}

			// Write the identifiers file
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(identifierFile), StandardCharsets.UTF_8)))
			{
				bw.write("# fjFile = PHENOTYPE");
				bw.newLine();
				bw.write("\tPUID\tSource Material Name\tSource Material PUID\tSynonyms");

				jhi.germinate.server.database.codegen.tables.Germinatebase g = GERMINATEBASE.as("g");
				Field<String> childName = GERMINATEBASE.NAME.as("childName");
				Field<String> childPuid = GERMINATEBASE.PUID.as("childPuid");
				Field<String> parentName = g.NAME.as("parentName");
				Field<String> parentPuid = g.PUID.as("parentPuid");
				Field<String[]> synonyms = SYNONYMS.SYNONYMS_.as("synonyms");

				SelectJoinStep<Record5<String, String, String, String, String[]>> step = context.select(
					childName,
					childPuid,
					parentName,
					parentPuid,
					synonyms
				).from(GERMINATEBASE.leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
									.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID))));

				// Restrict to the requested germplasm (if any)
				if (!CollectionUtils.isEmpty(germplasm))
					step.where(GERMINATEBASE.NAME.in(germplasm));
					// Otherwise, restrict it to everything in this dataset
				else
					step.where(DSL.exists(DSL.selectOne()
											 .from(DATASETMEMBERS)
											 .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID)
																			 .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
																			 .and(DATASETMEMBERS.DATASET_ID.eq(exportJob.getDatasetIds()[0])))));

				// Get only the ones where there's either an entity parent or the PUID or the synonyms are't null, otherwise we're wasting space in the file
				step.where(GERMINATEBASE.ENTITYPARENT_ID.isNotNull().or(GERMINATEBASE.PUID.isNotNull()).or(SYNONYMS.SYNONYMS_.isNotNull()));

				step.forEach(r -> {
					try
					{
						bw.newLine();
						bw.write(r.get(childName) + "\t");
						bw.write((r.get(childPuid) == null ? "" : r.get(childPuid)) + "\t");
						bw.write((r.get(parentName) == null ? "" : r.get(parentName)) + "\t");
						bw.write((r.get(parentPuid) == null ? "" : r.get(parentPuid)) + "\t");
						bw.write((r.get(synonyms)) == null ? "" : r.get(synonyms).toString());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				});
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		headers = exportJob.getJobConfig().getFileHeaders();
	}

	private List<String> run()
		throws IOException, SQLException
	{
		init();

		List<File> resultFiles = new ArrayList<>();
		resultFiles.add(tabbedBinnedFile);
		resultFiles.add(tabbedUnbinnedFile);

		if (mapFile != null)
			resultFiles.add(mapFile);
		if (identifierFile != null)
			resultFiles.add(identifierFile);

		// TODO
		int[] counts = new TabFileSubsetter().run(sourceFile, tabbedUnbinnedFile, germplasm, markers, headers);

		if (counts[0] == 0 || counts[1] == 0)
		{

		}
		else
		{
			try
			{
				BinData binData = new BinData(tabbedUnbinnedFile.getAbsolutePath(), tabbedBinnedFile.getAbsolutePath());
				switch (binningConfig.getBinningMethod())
				{
					case "equal":
						binData.writeStandardFile(binningConfig.getBinsLeft());
						break;
					case "split":
						binData.writeSplitFile(binningConfig.getBinsLeft(), binningConfig.getSplitPoint(), binningConfig.getBinsRight());
						break;
					case "auto":
						File histogramFile = new File(folder, projectName + ".hist");
						new MakeHistogram(200, tabbedUnbinnedFile.getAbsolutePath(), histogramFile.getAbsolutePath()).createHistogram();
						binData.writeAutoFile(binningConfig.getBinsLeft(), histogramFile.getAbsolutePath());
						histogramFile.delete();
						break;
				}
			}
			catch (Exception e)
			{

			}
		}

		List<String> logs = new ArrayList<>();

		if (flapjackProjectFile != null)
		{
			File tempTarget = Files.createTempFile(folder.getName(), ".flapjack").toFile();
			FlapjackFile project = new FlapjackFile(tempTarget.getAbsolutePath());
			CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedBinnedFile, mapFile, null, null, project, projectName);

			CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
			logs.addAll(createProject.doProjectCreation());

			Files.move(tempTarget.toPath(), flapjackProjectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			resultFiles.add(flapjackProjectFile);
		}

		FileUtils.zipUp(zipFile, resultFiles);

		return logs;
	}
}
