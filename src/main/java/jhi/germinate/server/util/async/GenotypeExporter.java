package jhi.germinate.server.util.async;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.util.Hdf5ToFJTabbedConverter;
import jhi.germinate.server.util.*;

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
	private String      headers = "";
	private String      projectName;

	public GenotypeExporter()
	{
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

	private List<String> run()
		throws IOException
	{
		init();

		List<File> resultFiles = new ArrayList<>();
		resultFiles.add(tabbedFile);

		if (mapFile != null)
			resultFiles.add(mapFile);
		if (identifierFile != null)
			resultFiles.add(identifierFile);

		// Extract from HDF5 to flat file
		Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File, germplasm, markers, tabbedFile.getAbsolutePath(), false);
		converter.extractData(headers);

		List<String> logs = new ArrayList<>();

		// Extract from HDF5 to HapMap
		if (hapmapFile != null)
		{
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

			Hdf5ToHapmapConverter hapmap = new Hdf5ToHapmapConverter(hdf5File, germplasm, markers, map, hapmapFile.getAbsolutePath());
			hapmap.extractData(null);

			resultFiles.add(hapmapFile);
		}
		// Create the Flapjack project
		if (flapjackProjectFile != null)
		{
			File tempTarget = Files.createTempFile(folder.getName(), ".flapjack").toFile();
			FlapjackFile project = new FlapjackFile(tempTarget.getAbsolutePath());
			CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedFile, mapFile, null, null, project, projectName);

			CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
			logs.addAll(createProject.doProjectCreation());

			Files.move(tempTarget.toPath(), flapjackProjectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			resultFiles.add(flapjackProjectFile);
		}

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);

		URI uri = URI.create("jar:file:/" + prefix);

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

		try (FileSystem fs = FileSystems.newFileSystem(uri, env, null))
		{
			for (File f : resultFiles)
			{
				Files.copy(f.toPath(), fs.getPath("/" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
				f.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return logs;
	}
}
