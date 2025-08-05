package jhi.germinate.server.util.async;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.DataExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.DataExportJobsRecord;
import jhi.germinate.server.database.pojo.AdditionalExportFormat;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.hdf5.Hdf5ToFJTabbedConverter;
import jhi.germinate.server.util.hdf5.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Mcpd.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExporter
{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private File                folder;
	private File                hdf5File;
	private File                hdf5TransposedFile;
	private File                mapFile;
	private File                tabbedFile;
	private File                flapjackProjectFile;
	private File                hapmapFile;
	private File                identifierFile;
	private File                zipFile;
	private Set<String>         germplasm;
	private Map<String, String> germplasmNameMapping = new HashMap<>();
	private Set<String>         markers;
	private String              headers         = "";
	private String              projectName;
	private boolean             includeFlatText = true;
	private DataExportJobs      exportJob;
	private Datasets            dataset;

	private final CountDownLatch latch;

	private final Instant         start;
	private final ExecutorService executor;

	private List<String> errors = new ArrayList<>();

	public GenotypeExporter()
	{
		start = Instant.now();
		latch = new CountDownLatch(3);

		executor = Executors.newCachedThreadPool();
	}

	public static void main(String[] args)
			throws IOException
	{
		GenotypeExporter exporter = new GenotypeExporter();
		Database.init(args[0], args[1], args[2], args[3], args[4], false);
		Integer jobId = Integer.parseInt(args[5]);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			DataExportJobsRecord job = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			job.setStatus(DataExportJobsStatus.running);
			job.store(DATA_EXPORT_JOBS.STATUS);
			exporter.exportJob = job.into(DataExportJobs.class);
			exporter.dataset = context.selectFrom(DATASETS).where(DATASETS.ID.eq(exporter.exportJob.getDatasetIds()[0])).fetchAnyInto(Datasets.class);

			exporter.hdf5File = new File(new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "data"), "genotypes"), exporter.dataset.getSourceFile());

			File potential = new File(exporter.hdf5File.getParentFile(), "transposed-" + exporter.hdf5File.getName());

			if (potential.exists() && potential.isFile())
				exporter.hdf5TransposedFile = potential;

			exporter.folder = new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "async"), exporter.exportJob.getUuid());
			exporter.projectName = "dataset-" + exporter.dataset.getId();

			if (exporter.exportJob.getJobConfig().getSubsetId() != null)
				exporter.projectName += "-map-" + exporter.exportJob.getJobConfig().getSubsetId();

			List<AdditionalExportFormat> formats = Arrays.asList(exporter.exportJob.getJobConfig().getFileTypes());

			exporter.tabbedFile = new File(exporter.folder, exporter.projectName + ".txt");
			exporter.zipFile = new File(exporter.folder, exporter.projectName + "-" + SDF.format(new Date()) + ".zip");

			if (exporter.exportJob.getJobConfig().getSubsetId() != null)
				exporter.mapFile = new File(exporter.folder, exporter.projectName + ".map");
			exporter.identifierFile = new File(exporter.folder, exporter.projectName + ".identifiers");

			if (formats.contains(AdditionalExportFormat.flapjack))
				exporter.flapjackProjectFile = new File(exporter.folder, exporter.projectName + ".flapjack");
			if (formats.contains(AdditionalExportFormat.hapmap))
				exporter.hapmapFile = new File(exporter.folder, exporter.projectName + ".hapmap");

			exporter.includeFlatText = formats.contains(AdditionalExportFormat.text);

			exporter.run();

			if (!CollectionUtils.isEmpty(exporter.errors))
				throw new IOException("Importer failed to run successfully. " + String.join("\n", exporter.errors));

			DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			record.setStatus(DataExportJobsStatus.completed);
			record.setResultSize(exporter.zipFile.length());
			record.store(DATA_EXPORT_JOBS.STATUS, DATA_EXPORT_JOBS.RESULT_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
				record.setStatus(DataExportJobsStatus.failed);
				record.store(DATA_EXPORT_JOBS.STATUS);
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

			// Get germplasm name mapping
			context.select(GERMINATEBASE.NAME, GERMINATEBASE.DISPLAY_NAME).from(GERMINATEBASE).forEach(g -> this.germplasmNameMapping.put(g.get(GERMINATEBASE.NAME), StringUtils.orElse(g.get(GERMINATEBASE.DISPLAY_NAME), g.get(GERMINATEBASE.NAME))));

			// Get the germplasm
			if (exportJob.getJobConfig().getYGroupIds() != null || exportJob.getJobConfig().getYIds() != null)
			{
				Set<String> result = new LinkedHashSet<>();

				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getYIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .where(GERMINATEBASE.ID.in(exportJob.getJobConfig().getYIds()))
										 .fetchInto(String.class));
				}
				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getYGroupIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
										 .where(GROUPMEMBERS.GROUP_ID.in(exportJob.getJobConfig().getYGroupIds()))
										 .fetchInto(String.class));
				}

				this.germplasm = result;
			}

			// Get the markers
			if (exportJob.getJobConfig().getXGroupIds() != null || exportJob.getJobConfig().getXIds() != null)
			{
				Set<String> result = new LinkedHashSet<>();
				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getXIds()))
				{
					result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
										 .from(MARKERS)
										 .where(MARKERS.ID.in(exportJob.getJobConfig().getXIds()))
										 .fetchInto(String.class));
				}

				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getXGroupIds()))
				{
					result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
										 .from(MARKERS)
										 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(MARKERS.ID))
										 .where(GROUPMEMBERS.GROUP_ID.in(exportJob.getJobConfig().getXGroupIds()))
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
				jhi.germinate.server.database.codegen.tables.Mcpd m = MCPD.as("m");
				Field<String> childName = GERMINATEBASE.NAME.as("childName");
				Field<String> childPuid = MCPD.PUID.as("childPuid");
				Field<String> parentName = g.NAME.as("parentName");
				Field<String> parentPuid = m.PUID.as("parentPuid");
				Field<String[]> synonyms = SYNONYMS.SYNONYMS_.as("synonyms");

				SelectJoinStep<Record5<String, String, String, String, String[]>> step = context.select(
						childName,
						childPuid,
						parentName,
						parentPuid,
						synonyms
				).from(GERMINATEBASE.leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
									.leftJoin(MCPD).on(MCPD.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
									.leftJoin(m).on(m.GERMINATEBASE_ID.eq(g.ID))
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
				step.where(GERMINATEBASE.ENTITYPARENT_ID.isNotNull().or(MCPD.PUID.isNotNull()).or(SYNONYMS.SYNONYMS_.isNotNull()));

				step.forEach(r -> {
					try
					{
						bw.newLine();
						bw.write(r.get(childName) + "\t");
						bw.write((r.get(childPuid) == null ? "" : r.get(childPuid)) + "\t");
						bw.write((r.get(parentName) == null ? "" : r.get(parentName)) + "\t");
						bw.write((r.get(parentPuid) == null ? "" : r.get(parentPuid)) + "\t");
						bw.write((r.get(synonyms)) == null ? "" : Arrays.toString(r.get(synonyms)));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				});
			}
			catch (IOException e)
			{
				errors.add(e.getMessage());
				e.printStackTrace();
			}
		}

		headers = exportJob.getJobConfig().getFileHeaders();
	}

	private void zip(FileSystem fs, File f, boolean delete)
			throws IOException
	{
		Files.copy(f.toPath(), fs.getPath("/" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
		if (delete) f.delete();
	}

	private List<String> run()
			throws IOException, SQLException
	{
		init();

		// Make sure it doesn't exist
		if (zipFile.exists()) zipFile.delete();

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/")) prefix = prefix.substring(1);
		URI uri = URI.create("jar:file:/" + prefix);
		Map<String, Object> env = new HashMap<>();
		// We need to write this to a temp file, because genotypic data is potentially much larger than the memory and by default ZipFileSystem
		// generates the Zip in memory: https://stackoverflow.com/questions/23858706/zipping-a-huge-folder-by-using-a-zipfilesystem-results-in-outofmemoryerror
		// This has to be a String that's "true"
		env.put("create", "true");
		env.put("encoding", "UTF-8");
		// This has to be a Boolean that's true.
		env.put("useTempFile", Boolean.TRUE);

		List<String> logs = new ArrayList<>();

		try (FileSystem fs = FileSystems.newFileSystem(uri, env, null))
		{
			// No Flapjack, so we can speed things up
			if (flapjackProjectFile == null)
			{
				// If the flat file has been requested, export it
				if (includeFlatText)
				{
					executor.execute(() -> {
						System.out.println("EXTRACTING FLAT FILE INTO ZIP");
						try
						{
							Path tabbedZipped = fs.getPath("/" + tabbedFile.getName());
							// Extract from HDF5 to flat file (zipped)
							Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File.toPath(), germplasm, markers, germplasmNameMapping, tabbedZipped, false);
							converter.extractData(headers);
						}
						finally
						{
							latch.countDown();
						}
					});
				}
				else
				{
					// Otherwise, count down
					latch.countDown();
				}

				// Extract from HDF5 to HapMap
				if (hapmapFile != null)
				{
					exportHapmap(fs);
				}
				else
				{
					latch.countDown();
				}

				// Start copying the files into the zip
				zipUp(fs, false, includeFlatText);
			}
			else
			{
				// Create the Flapjack project (this will first extract to the flat file, then create the flapjack file, then zip both if required)
				exportFlapjack(fs, logs);

				// Extract from HDF5 to HapMap
				if (hapmapFile != null)
				{
					exportHapmap(fs);
				}
				else
				{
					latch.countDown();
				}

				// Start copying the files into the zip, don't zip the flat file yet, the flapjack task needs to generate it first
				zipUp(fs, false, includeFlatText);
			}

			// Wait for everything to finish
			latch.await();

			// Now delete the remaining files
			if (mapFile != null) mapFile.delete();
			if (tabbedFile != null) tabbedFile.delete();

			Duration duration = Duration.between(start, Instant.now());
			System.out.println("DURATION: " + duration);
		}
		catch (IOException | InterruptedException e)
		{
			errors.add(e.getMessage());
			e.printStackTrace();
		}

		executor.shutdown();

		return logs;
	}

	private void zipUp(FileSystem fs, boolean includeTabbed, boolean includeMap)
	{
		executor.execute(() -> {
			try
			{
				if (includeTabbed)
				{
					System.out.println("ZIPPING UP TABBED");
					// Zip and don't delete the tabbed file
					zip(fs, tabbedFile, false);
				}

				// Zip and don't delete the map
				if (includeMap && mapFile != null)
				{
					System.out.println("ZIPPING UP MAP");
					zip(fs, mapFile, false);
				}
				// Zip and delete the identifiers
				if (identifierFile != null)
				{
					System.out.println("ZIPPING UP IDENTIFIERS");
					zip(fs, identifierFile, true);
				}
			}
			catch (IOException e)
			{
				errors.add(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				latch.countDown();
			}
		});
	}

	private void exportFlapjack(FileSystem fs, List<String> logs)
	{
		executor.execute(() -> {
			try
			{
				System.out.println("EXTRACTING TO TABBED");
				// Extract from HDF5 to flat file (not zipped)
				Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File.toPath(), germplasm, markers, germplasmNameMapping, tabbedFile.toPath(), false);
				converter.extractData(headers);

				System.out.println("CONVERTING TO FLAPJACK");
				File tempTarget = Files.createTempFile(folder.getName(), ".flapjack").toFile();
				FlapjackFile project = new FlapjackFile(tempTarget.getAbsolutePath());
				CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedFile, mapFile, null, null, project, projectName);

				CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
				logs.addAll(createProject.doProjectCreation());

				Files.move(tempTarget.toPath(), flapjackProjectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				if (includeFlatText) zip(fs, tabbedFile, true);

				zip(fs, flapjackProjectFile, true);
			}
			catch (IOException e)
			{
				errors.add(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				latch.countDown();
			}
		});
	}

	private void exportHapmap(FileSystem fs)
	{
		executor.execute(() -> {
			try
			{
				System.out.println("EXTRACTING HAPMAP INTO ZIP");
				Map<String, Hdf5ToHapmapConverter.MarkerPosition> map = new HashMap<>();
				if (mapFile != null && mapFile.exists())
				{
					Files.readAllLines(mapFile.toPath()).stream().skip(1).filter(l -> !StringUtils.isEmpty(l)).forEachOrdered(m -> {
						String[] parts = m.split("\t");
						map.put(parts[0], new Hdf5ToHapmapConverter.MarkerPosition(parts[1], Integer.toString((int) Math.round(Double.parseDouble(parts[2])))));
					});
				}

				Path hapmapPath = fs.getPath("/" + hapmapFile.getName());

				AbstractHdf5Converter converter;

				if (hdf5TransposedFile != null)
					converter = new Hdf5TransposedToHapmapConverter(hdf5TransposedFile.toPath(), germplasm, markers, map, germplasmNameMapping, hapmapPath);
				else
					converter = new Hdf5ToHapmapConverter(hdf5File.toPath(), germplasm, markers, map, germplasmNameMapping, hapmapPath);

				converter.extractData(null);
			}
			catch (IOException e)
			{
				errors.add(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				latch.countDown();
			}
		});
	}
}
