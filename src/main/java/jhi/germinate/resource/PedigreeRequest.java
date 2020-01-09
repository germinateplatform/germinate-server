package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PedigreeRequest
{
	private Integer[] individualIds;
	private Integer[] groupIds;
	private Integer   levelsUp;
	private Integer   levelsDown;

	public Integer[] getIndividualIds()
	{
		return individualIds;
	}

	public PedigreeRequest setIndividualIds(Integer[] individualIds)
	{
		this.individualIds = individualIds;
		return this;
	}

	public Integer[] getGroupIds()
	{
		return groupIds;
	}

	public PedigreeRequest setGroupIds(Integer[] groupIds)
	{
		this.groupIds = groupIds;
		return this;
	}

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
}
