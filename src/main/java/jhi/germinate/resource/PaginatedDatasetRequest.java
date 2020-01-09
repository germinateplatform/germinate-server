package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PaginatedDatasetRequest extends PaginatedRequest
{
	private Integer[] datasetIds;

	public Integer[] getDatasetIds()
	{
		return datasetIds;
	}

	public PaginatedDatasetRequest setDatasetIds(Integer[] datasetIds)
	{
		this.datasetIds = datasetIds;
		return this;
	}
}
