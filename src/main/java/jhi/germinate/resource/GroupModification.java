package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class GroupModification
{
	private Integer[] ids;
	private boolean   isAddition;

	public GroupModification()
	{
	}

	public Integer[] getIds()
	{
		return ids;
	}

	public GroupModification setIds(Integer[] ids)
	{
		this.ids = ids;
		return this;
	}

	public boolean isAddition()
	{
		return isAddition;
	}

	public GroupModification setAddition(boolean addition)
	{
		isAddition = addition;
		return this;
	}
}
