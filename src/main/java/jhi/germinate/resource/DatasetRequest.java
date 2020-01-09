package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetRequest
{
	private Integer[] datasetIds;

	public Integer[] getDatasetIds()
	{
		return datasetIds;
	}

	public DatasetRequest setDatasetIds(Integer[] datasetIds)
	{
		this.datasetIds = datasetIds;
		return this;
	}
}
