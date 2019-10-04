package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupRequest extends DatasetRequest
{
	private String experimentType;
	private String groupType;

	public String getExperimentType()
	{
		return experimentType;
	}

	public DatasetGroupRequest setExperimentType(String experimentType)
	{
		this.experimentType = experimentType;
		return this;
	}

	public String getGroupType()
	{
		return groupType;
	}

	public DatasetGroupRequest setGroupType(String groupType)
	{
		this.groupType = groupType;
		return this;
	}
}
