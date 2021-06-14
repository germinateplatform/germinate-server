package jhi.germinate.resource;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupModificationRequest
{
	private Integer   datasetId;
	private Integer[] groupIds;
	private Boolean   isAddOperation;

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

	@JsonGetter("isAddOperation")
	public Boolean isAddOperation()
	{
		return isAddOperation;
	}

	public DatasetGroupModificationRequest setAddOperation(Boolean addOperation)
	{
		isAddOperation = addOperation;
		return this;
	}
}
