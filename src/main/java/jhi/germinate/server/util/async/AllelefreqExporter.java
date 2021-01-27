package jhi.germinate.server.util.async;

import com.google.gson.Gson;
import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.binning.*;
import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.util.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
	private File markersFile;
	private File headerFile;
	private File flapjackProjectFile;
	private File identifierFile;
	private File zipFile;

	private Set<String>   germplasm;
	private Set<String>   markers;
	private BinningConfig binningConfig;
	private String        headers = "";
	private String        projectName;

	public AllelefreqExporter()
	{
	}

	public static void main(String[] args)
		throws IOException
	{
		int i = 0;
		AllelefreqExporter exporter = new AllelefreqExporter();
		exporter.sourceFile = new File(args[i++]);

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

		exporter.tabbedBinnedFile = new File(exporter.folder, exporter.projectName + ".txt");
		exporter.tabbedUnbinnedFile = new File(exporter.folder, exporter.projectName + "-unbinned.txt");
		exporter.zipFile = new File(exporter.folder, exporter.projectName + "-" + SDF.format(new Date()) + ".zip");

		File germplasmFile = new File(exporter.folder, exporter.projectName + ".germplasm");
		File markersFile = new File(exporter.folder, exporter.projectName + ".markers");
		File headersFile = new File(exporter.folder, exporter.projectName + ".header");
		File mapFile = new File(exporter.folder, exporter.projectName + ".map");
		File binningConfigFile = new File(exporter.folder, exporter.projectName + ".json");
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
		if (binningConfigFile.exists() && binningConfigFile.isFile())
		{
			exporter.binningConfig = new Gson().fromJson(new FileReader(binningConfigFile), BinningConfig.class);
			binningConfigFile.delete();
		}
		else
		{
			exporter.binningConfig = BinningConfig.DEFAULT;
		}
		if (formats.contains(AdditionalExportFormat.flapjack))
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
