package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetUserModificationRequest
{
	private Integer   datasetId;
	private Integer[] userIds;
	private Boolean   addOperation;

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public DatasetUserModificationRequest setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public Integer[] getUserIds()
	{
		return userIds;
	}

	public DatasetUserModificationRequest setUserIds(Integer[] userIds)
	{
		this.userIds = userIds;
		return this;
	}

	public Boolean getAddOperation()
	{
		return addOperation;
	}

	public DatasetUserModificationRequest setAddOperation(Boolean addOperation)
	{
		this.addOperation = addOperation;
		return this;
	}
}
