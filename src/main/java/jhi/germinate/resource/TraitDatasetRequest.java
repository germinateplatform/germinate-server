package jhi.germinate.resource;

public class TraitDatasetRequest extends DatasetRequest
{
	private Integer[] traitIds;

	public Integer[] getTraitIds()
	{
		return traitIds;
	}

	public TraitDatasetRequest setTraitIds(Integer[] traitIds)
	{
		this.traitIds = traitIds;
		return this;
	}
}
