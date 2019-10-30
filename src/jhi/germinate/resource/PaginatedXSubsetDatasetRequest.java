package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PaginatedXSubsetDatasetRequest extends PaginatedDatasetRequest
{
	private Integer[] xIds;

	public Integer[] getxIds()
	{
		return xIds;
	}

	public PaginatedXSubsetDatasetRequest setxIds(Integer[] xIds)
	{
		this.xIds = xIds;
		return this;
	}
}
