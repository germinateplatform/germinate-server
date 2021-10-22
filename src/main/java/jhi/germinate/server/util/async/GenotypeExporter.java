package jhi.germinate.server.util.async;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.hdf5.Hdf5ToFJTabbedConverter;
import jhi.germinate.server.util.hdf5.*;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExporter
{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private File        folder;
	private File        hdf5File;
	private File        mapFile;
	private File        tabbedFile;
	private File        germplasmFile;
	private File        markersFile;
	private File        headerFile;
	private File        flapjackProjectFile;
	private File        hapmapFile;
	private File        identifierFile;
	private File        zipFile;
	private Set<String> germplasm;
	private Set<String> markers;
	private String      headers         = "";
	private String      projectName;
	private boolean     includeFlatText = true;

	private final CountDownLatch latch;

	private final Instant start;
	private final ExecutorService executor;

	public GenotypeExporter()
	{
		start = Instant.now();
		latch = new CountDownLatch(3);

		executor = Executors.newCachedThreadPool();
	}

	public static void main(String[] args)
		throws IOException
	{
		int i = 0;
		GenotypeExporter exporter = new GenotypeExporter();
		exporter.hdf5File = new File(args[i++]);

		exporter.folder = new File(args[i++]);
		exporter.projectName = args[i++];
		String additionalFormats = args[i++];
		String[] parts = additionalFormats.split(",");
		List<AdditionalExportFormat> formats = Arrays.stream(parts)
													 .map(p -> {
														 try
														 {
															 return AdditionalExportFormat.valueOf(p);
														 }
														 catch (Exception e)
														 {
															 return null;
														 }
													 })
													 .filter(Objects::nonNull)
													 .collect(Collectors.toList());

		exporter.tabbedFile = new File(exporter.folder, exporter.projectName + ".txt");
		exporter.zipFile = new File(exporter.folder, exporter.projectName + "-" + SDF.format(new Date()) + ".zip");

		File germplasmFile = new File(exporter.folder, exporter.projectName + ".germplasm");
		File markersFile = new File(exporter.folder, exporter.projectName + ".markers");
		File headersFile = new File(exporter.folder, exporter.projectName + ".header");
		File mapFile = new File(exporter.folder, exporter.projectName + ".map");
		File identifierFile = new File(exporter.folder, exporter.projectName + ".identifiers");

		if (germplasmFile.exists() && germplasmFile.isFile())
			exporter.germplasmFile = germplasmFile;
		if (markersFile.exists() && markersFile.isFile())
			exporter.markersFile = markersFile;
		if (headersFile.exists() && headersFile.isFile())
			exporter.headerFile = headersFile;
		if (identifierFile.exists() && identifierFile.isFile())
			exporter.identifierFile = identifierFile;
		if (mapFile.exists() && mapFile.isFile())
			exporter.mapFile = mapFile;
		else
			exporter.mapFile = null;
		if (formats.contains(AdditionalExportFormat.flapjack))
			exporter.flapjackProjectFile = new File(exporter.folder, exporter.projectName + ".flapjack");
		if (formats.contains(AdditionalExportFormat.hapmap))
			exporter.hapmapFile = new File(exporter.folder, exporter.projectName + ".hapmap");

		exporter.includeFlatText = formats.contains(AdditionalExportFormat.text);

		exporter.run();
	}

	private void init()
		throws IOException
	{
		if (germplasmFile != null)
		{
			germplasm = new LinkedHashSet<>(Files.readAllLines(germplasmFile.toPath()));
			germplasmFile.delete();
		}

		if (markersFile != null)
		{
			markers = new LinkedHashSet<>(Files.readAllLines(markersFile.toPath()));
			markersFile.delete();
		}

		if (headerFile != null)
		{
			headers = String.join("\n", Files.readAllLines(headerFile.toPath())) + "\n";
			headerFile.delete();
		}
	}

	private void zip(FileSystem fs, File f, boolean delete)
		throws IOException
	{
		Files.copy(f.toPath(), fs.getPath("/" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
		if (delete)
			f.delete();
	}

	private List<String> run()
		throws IOException
	{
		init();

		// Make sure it doesn't exist
		if (zipFile.exists())
			zipFile.delete();

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);
		URI uri = URI.create("jar:file:/" + prefix);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

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
							Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File.toPath(), germplasm, markers, tabbedZipped, false);
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
				// Create the Flapjack project
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

				// Start copying the files into the zip
				zipUp(fs, false, includeFlatText);
			}

			// Wait for everything to finish
			latch.await();

			// Now delete the remaining files
			if (mapFile != null)
				mapFile.delete();
			if (tabbedFile != null)
				tabbedFile.delete();

			Duration duration = Duration.between(start, Instant.now());
			System.out.println("DURATION: " + duration);
		}
		catch (IOException | InterruptedException e)
		{
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
				Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File.toPath(), germplasm, markers, tabbedFile.toPath(), false);
				converter.extractData(headers);

				System.out.println("CONVERTING TO FLAPJACK");
				File tempTarget = Files.createTempFile(folder.getName(), ".flapjack").toFile();
				FlapjackFile project = new FlapjackFile(tempTarget.getAbsolutePath());
				CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedFile, mapFile, null, null, project, projectName);

				CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
				logs.addAll(createProject.doProjectCreation());

				Files.move(tempTarget.toPath(), flapjackProjectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				if (includeFlatText)
					zip(fs, tabbedFile, true);

				zip(fs, flapjackProjectFile, true);
			}
			catch (IOException e)
			{
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
				if (mapFile != null)
				{
					Files.readAllLines(mapFile.toPath())
						 .stream()
						 .skip(1)
						 .filter(l -> !StringUtils.isEmpty(l))
						 .forEachOrdered(m -> {
							 String[] parts = m.split("\t");
							 map.put(parts[0], new Hdf5ToHapmapConverter.MarkerPosition(parts[1], Integer.toString((int) Math.round(Double.parseDouble(parts[2])))));
						 });
				}

				Path hapmapPath = fs.getPath("/" + hapmapFile.getName());

				Hdf5ToHapmapConverter hapmap = new Hdf5ToHapmapConverter(hdf5File.toPath(), germplasm, markers, map, hapmapPath);
				hapmap.extractData(null);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				latch.countDown();
			}
		});
	}
}
