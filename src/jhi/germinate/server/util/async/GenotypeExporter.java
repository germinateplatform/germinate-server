package jhi.germinate.server.util.async;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.util.Hdf5ToFJTabbedConverter;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExporter
{
	private File        folder;
	private File        hdf5File;
	private File        mapFile;
	private File        tabbedFile;
	private File        germplasmFile;
	private File        markersFile;
	private File        headerFile;
	private File        flapjackProjectFile;
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
		boolean createFlapjackProject = Boolean.parseBoolean(args[i++]);

		exporter.tabbedFile = new File(exporter.folder, exporter.projectName + ".txt");
		exporter.zipFile = new File(exporter.folder, exporter.projectName + ".zip");

		File germplasmFile = new File(exporter.folder, exporter.projectName + ".germplasm");
		File markersFile = new File(exporter.folder, exporter.projectName + ".markers");
		File headersFile = new File(exporter.folder, exporter.projectName + ".header");
		File mapFile = new File(exporter.folder, exporter.projectName + ".map");

		if (germplasmFile.exists() && germplasmFile.isFile())
			exporter.germplasmFile = germplasmFile;
		if (markersFile.exists() && markersFile.isFile())
			exporter.markersFile = markersFile;
		if (headersFile.exists() && headersFile.isFile())
			exporter.headerFile = headersFile;
		if (mapFile.exists() && mapFile.isFile())
			exporter.mapFile = mapFile;
		else
			exporter.mapFile = null;
		if (createFlapjackProject)
			exporter.flapjackProjectFile = new File(exporter.folder, exporter.projectName + ".flapjack");


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

		Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File, germplasm, markers, tabbedFile.getAbsolutePath(), false);
		converter.readInput();
		converter.extractData(headers);

		List<String> logs = new ArrayList<>();

		if (flapjackProjectFile != null)
		{
			FlapjackFile project = new FlapjackFile(flapjackProjectFile.getAbsolutePath());
			CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedFile, mapFile, null, null, project, projectName);

			CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
			logs.addAll(createProject.doProjectCreation());

			resultFiles.add(flapjackProjectFile);
		}

		URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));

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
