package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class SubsettedGenotypeDatasetRequest extends SubsettedDatasetRequest
{
	private Integer mapId;
	private boolean generateFlapjackProject;
	private boolean generateHapMap;

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
}
