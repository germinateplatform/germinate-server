package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;

/**
 * @author Sebastian Raubach
 */
public class ViewTableGroupLocations extends ViewTableLocations
{
	private Integer groupId;

	public Integer getGroupId()
	{
		return groupId;
	}

	public ViewTableGroupLocations setGroupId(Integer groupId)
	{
		this.groupId = groupId;
		return this;
	}
}
