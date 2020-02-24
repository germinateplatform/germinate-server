package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupRequest extends DatasetRequest
{
	private String datasetType;
	private String groupType;

	public String getDatasetType()
	{
		return datasetType;
	}

	public DatasetGroupRequest setDatasetType(String datasetType)
	{
		this.datasetType = datasetType;
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
