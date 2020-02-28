package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class GroupModificationRequest
{
	private Integer[] ids;
	private boolean   isAddition;

	public GroupModificationRequest()
	{
	}

	public Integer[] getIds()
	{
		return ids;
	}

	public GroupModificationRequest setIds(Integer[] ids)
	{
		this.ids = ids;
		return this;
	}

	public boolean isAddition()
	{
		return isAddition;
	}

	public GroupModificationRequest setAddition(boolean addition)
	{
		isAddition = addition;
		return this;
	}
}
