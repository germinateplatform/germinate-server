package jhi.germinate.resource;

import jhi.germinate.server.database.tables.pojos.ViewTableGermplasm;

/**
 * @author Sebastian Raubach
 */
public class ViewTableGroupGermplasm extends ViewTableGermplasm
{
	private Integer groupId;

	public Integer getGroupId()
	{
		return groupId;
	}

	public ViewTableGroupGermplasm setGroupId(Integer groupId)
	{
		this.groupId = groupId;
		return this;
	}
}
