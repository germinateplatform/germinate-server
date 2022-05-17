package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class GroupModificationRequest
{
	private Integer[] ids;
	private Boolean   addition;

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

	public Boolean getAddition()
	{
		return addition;
	}

	public GroupModificationRequest setAddition(Boolean addition)
	{
		this.addition = addition;
		return this;
	}
}
