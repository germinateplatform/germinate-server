package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;

import java.util.List;

public class ViewTableClimateDataWithGroups extends ViewTableClimateData
{
	private List<Groups> groups;

	public List<Groups> getGroups()
	{
		return groups;
	}

	public ViewTableClimateDataWithGroups setGroups(List<Groups> groups)
	{
		this.groups = groups;
		return this;
	}
}
