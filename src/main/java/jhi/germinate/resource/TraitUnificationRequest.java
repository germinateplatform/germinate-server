package jhi.germinate.resource;

public class TraitUnificationRequest
{
	private Integer   preferredTraitId;
	private Integer[] otherTraitIds;

	public Integer getPreferredTraitId()
	{
		return preferredTraitId;
	}

	public TraitUnificationRequest setPreferredTraitId(Integer preferredTraitId)
	{
		this.preferredTraitId = preferredTraitId;
		return this;
	}

	public Integer[] getOtherTraitIds()
	{
		return otherTraitIds;
	}

	public TraitUnificationRequest setOtherTraitIds(Integer[] otherTraitIds)
	{
		this.otherTraitIds = otherTraitIds;
		return this;
	}
}
