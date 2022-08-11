package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.AdditionalExportFormat;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class SubsettedGenotypeDatasetRequest extends SubsettedDatasetRequest
{
	private Integer mapId;
	private boolean generateFlapjackProject;
	private boolean generateHapMap;
	private boolean generateFlatFile;

	public Integer getMapId()
	{
		return mapId;
	}

	public SubsettedGenotypeDatasetRequest setMapId(Integer mapId)
	{
		this.mapId = mapId;
		return this;
	}

	public boolean isGenerateFlapjackProject()
	{
		return generateFlapjackProject;
	}

	public SubsettedGenotypeDatasetRequest setGenerateFlapjackProject(boolean generateFlapjackProject)
	{
		this.generateFlapjackProject = generateFlapjackProject;
		return this;
	}

	public boolean isGenerateHapMap()
	{
		return generateHapMap;
	}

	public SubsettedGenotypeDatasetRequest setGenerateHapMap(boolean generateHapMap)
	{
		this.generateHapMap = generateHapMap;
		return this;
	}

	public boolean isGenerateFlatFile()
	{
		return generateFlatFile;
	}

	public SubsettedGenotypeDatasetRequest setGenerateFlatFile(boolean generateFlatFile)
	{
		this.generateFlatFile = generateFlatFile;
		return this;
	}

	public AdditionalExportFormat[] getFileTypes() {
		List<AdditionalExportFormat> result = new ArrayList<>();

		if (generateFlapjackProject)
			result.add(AdditionalExportFormat.flapjack);
		if (generateFlatFile)
			result.add(AdditionalExportFormat.text);
		if (generateHapMap)
			result.add(AdditionalExportFormat.hapmap);

		return result.toArray(new AdditionalExportFormat[0]);
	}
}
