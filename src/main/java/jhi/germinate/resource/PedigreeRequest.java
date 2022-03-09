package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PedigreeRequest extends SubsettedDatasetRequest
{
	private Integer   levelsUp;
	private Integer   levelsDown;
	private Boolean   includeAttributes;

	public Integer getLevelsUp()
	{
		return levelsUp;
	}

	public PedigreeRequest setLevelsUp(Integer levelsUp)
	{
		this.levelsUp = levelsUp;
		return this;
	}

	public Integer getLevelsDown()
	{
		return levelsDown;
	}

	public PedigreeRequest setLevelsDown(Integer levelsDown)
	{
		this.levelsDown = levelsDown;
		return this;
	}

	public Boolean getIncludeAttributes()
	{
		return includeAttributes;
	}

	public PedigreeRequest setIncludeAttributes(Boolean includeAttributes)
	{
		this.includeAttributes = includeAttributes;
		return this;
	}
}
