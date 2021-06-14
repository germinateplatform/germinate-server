package jhi.germinate.resource;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Arrays;

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

	@JsonGetter("isAddition")
	public boolean isAddition()
	{
		return isAddition;
	}

	public GroupModificationRequest setAddition(boolean addition)
	{
		isAddition = addition;
		return this;
	}

	@Override
	public String toString()
	{
		return "GroupModificationRequest{" +
			"ids=" + Arrays.toString(ids) +
			", isAddition=" + isAddition +
			'}';
	}
}
