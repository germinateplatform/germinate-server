package jhi.germinate.resource;

import java.util.List;

public class ViewTableTrialGermplasm extends ViewTableGermplasm
{
	private List<Integer> groupIds;

	public List<Integer> getGroupIds()
	{
		return groupIds;
	}

	public ViewTableTrialGermplasm setGroupIds(List<Integer> groupIds)
	{
		this.groupIds = groupIds;
		return this;
	}
}
