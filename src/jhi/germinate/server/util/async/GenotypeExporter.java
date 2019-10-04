package jhi.germinate.server.util.async;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.util.Hdf5ToFJTabbedConverter;
import jhi.germinate.server.util.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExporter
{
	private File hdf5File;
	private File mapFile;
	private File tabbedFile;
	private File germplasmFile;
	private File markersFile;
	private File headerFile;
	private File outputFile;
	private Set<String> germplasm;
	private Set<String> markers;
	private String      headers = "";
	public GenotypeExporter()
	{
	}

	public static void main(String[] args)
		throws IOException
	{
		int i = 0;
		String part;
		GenotypeExporter exporter = new GenotypeExporter();
		exporter.hdf5File = new File(args[i++]);
		exporter.mapFile = new File(args[i++]);
		exporter.tabbedFile = new File(args[i++]);
		part = args[i++];
		if (!StringUtils.isEmpty(part))
			exporter.germplasmFile = new File(part);
		part = args[i++];
		if (!StringUtils.isEmpty(part))
			exporter.markersFile = new File(part);
		part = args[i++];
		if (!StringUtils.isEmpty(part))
			exporter.headerFile = new File(part);
		exporter.outputFile = new File(args[i++]);

		exporter.run();
	}

	private void init()
		throws IOException
	{
		if (germplasmFile != null && CollectionUtils.isEmpty(germplasm))
		{
			germplasm = new LinkedHashSet<>(Files.readAllLines(germplasmFile.toPath()));
			germplasmFile.delete();
		}

		if (markersFile != null && CollectionUtils.isEmpty(markers))
		{
			markers = new LinkedHashSet<>(Files.readAllLines(markersFile.toPath()));
			markersFile.delete();
		}

		if (headerFile != null && headers == null)
		{
			headers = String.join("\n", Files.readAllLines(headerFile.toPath()));
			headerFile.delete();
		}
	}

	public List<String> run()
		throws IOException
	{
		init();

		Hdf5ToFJTabbedConverter converter = new Hdf5ToFJTabbedConverter(hdf5File, germplasm, markers, tabbedFile.getAbsolutePath(), false);
		converter.readInput();
		converter.extractData(headers);

		FlapjackFile project = new FlapjackFile(outputFile.getAbsolutePath());

		CreateProjectSettings cpSettings = new CreateProjectSettings(tabbedFile, mapFile, null, null, project, "Germinate"); // TODO: Name

		CreateProject createProject = new CreateProject(cpSettings, new DataImportSettings());
		List<String> logs = createProject.doProjectCreation();

		return logs;
	}

	public File getHdf5File()
	{
		return hdf5File;
	}

	public GenotypeExporter setHdf5File(File hdf5File)
	{
		this.hdf5File = hdf5File;
		return this;
	}

	public File getMapFile()
	{
		return mapFile;
	}

	public GenotypeExporter setMapFile(File mapFile)
	{
		this.mapFile = mapFile;
		return this;
	}

	public File getTabbedFile()
	{
		return tabbedFile;
	}

	public GenotypeExporter setTabbedFile(File tabbedFile)
	{
		this.tabbedFile = tabbedFile;
		return this;
	}

	public File getGermplasmFile()
	{
		return germplasmFile;
	}

	public GenotypeExporter setGermplasmFile(File germplasmFile)
	{
		this.germplasmFile = germplasmFile;
		return this;
	}

	public File getMarkersFile()
	{
		return markersFile;
	}

	public GenotypeExporter setMarkersFile(File markersFile)
	{
		this.markersFile = markersFile;
		return this;
	}

	public File getHeaderFile()
	{
		return headerFile;
	}

	public GenotypeExporter setHeaderFile(File headerFile)
	{
		this.headerFile = headerFile;
		return this;
	}

	public File getOutputFile()
	{
		return outputFile;
	}

	public GenotypeExporter setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
		return this;
	}

	public Set<String> getGermplasm()
	{
		return germplasm;
	}

	public GenotypeExporter setGermplasm(Set<String> germplasm)
	{
		this.germplasm = germplasm;
		return this;
	}

	public Set<String> getMarkers()
	{
		return markers;
	}

	public GenotypeExporter setMarkers(Set<String> markers)
	{
		this.markers = markers;
		return this;
	}

	public String getHeaders()
	{
		return headers;
	}

	public GenotypeExporter setHeaders(String headers)
	{
		this.headers = headers;
		return this;
	}
}
