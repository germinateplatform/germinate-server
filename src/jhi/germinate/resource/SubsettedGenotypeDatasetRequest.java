package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class SubsettedGenotypeDatasetRequest extends SubsettedDatasetRequest
{
	private Integer mapId;

	public Integer getMapId()
	{
		return mapId;
	}

	public SubsettedGenotypeDatasetRequest setMapId(Integer mapId)
	{
		this.mapId = mapId;
		return this;
	}
}
