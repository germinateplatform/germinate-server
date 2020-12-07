package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMarkers;

/**
 * @author Sebastian Raubach
 */
public class ViewTableGroupMarkers extends ViewTableMarkers
{
	private Integer groupId;

	public Integer getGroupId()
	{
		return groupId;
	}

	public ViewTableGroupMarkers setGroupId(Integer groupId)
	{
		this.groupId = groupId;
		return this;
	}
}
