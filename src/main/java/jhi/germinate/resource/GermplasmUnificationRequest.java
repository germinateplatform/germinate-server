package jhi.germinate.resource;

public class GermplasmUnificationRequest
{
	private Integer   preferredGermplasmId;
	private Integer[] otherGermplasmIds;
	private String    explanation;

	public Integer getPreferredGermplasmId()
	{
		return preferredGermplasmId;
	}

	public GermplasmUnificationRequest setPreferredGermplasmId(Integer preferredGermplasmId)
	{
		this.preferredGermplasmId = preferredGermplasmId;
		return this;
	}

	public Integer[] getOtherGermplasmIds()
	{
		return otherGermplasmIds;
	}

	public GermplasmUnificationRequest setOtherGermplasmIds(Integer[] otherGermplasmIds)
	{
		this.otherGermplasmIds = otherGermplasmIds;
		return this;
	}

	public String getExplanation()
	{
		return explanation;
	}

	public GermplasmUnificationRequest setExplanation(String explanation)
	{
		this.explanation = explanation;
		return this;
	}
}
