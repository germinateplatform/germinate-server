package jhi.germinate.resource;

import java.util.List;

public class TraitTimelineRequest
{
	private List<Integer> datasetIds;
	private List<Integer> traitIds;
	private List<Integer> groupIds;
	private List<Integer> markedIds;

	public List<Integer> getDatasetIds()
	{
		return datasetIds;
	}

	public TraitTimelineRequest setDatasetIds(List<Integer> datasetIds)
	{
		this.datasetIds = datasetIds;
		return this;
	}

	public List<Integer> getTraitIds()
	{
		return traitIds;
	}

	public TraitTimelineRequest setTraitIds(List<Integer> traitIds)
	{
		this.traitIds = traitIds;
		return this;
	}

	public List<Integer> getGroupIds()
	{
		return groupIds;
	}

	public TraitTimelineRequest setGroupIds(List<Integer> groupIds)
	{
		this.groupIds = groupIds;
		return this;
	}

	public List<Integer> getMarkedIds()
	{
		return markedIds;
	}

	public TraitTimelineRequest setMarkedIds(List<Integer> markedIds)
	{
		this.markedIds = markedIds;
		return this;
	}
}
