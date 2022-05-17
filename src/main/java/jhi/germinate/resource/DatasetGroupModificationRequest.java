package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupModificationRequest
{
	private Integer   datasetId;
	private Integer[] groupIds;
	private Boolean   addOperation;

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public DatasetGroupModificationRequest setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public Integer[] getGroupIds()
	{
		return groupIds;
	}

	public DatasetGroupModificationRequest setGroupIds(Integer[] groupIds)
	{
		this.groupIds = groupIds;
		return this;
	}

	public Boolean getAddOperation()
	{
		return addOperation;
	}

	public DatasetGroupModificationRequest setAddOperation(Boolean addOperation)
	{
		this.addOperation = addOperation;
		return this;
	}
}
