package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PaginatedTraitDatasetRequest extends PaginatedDatasetRequest
{
	private Integer[] traitIds;

	public Integer[] getTraitIds()
	{
		return traitIds;
	}

	public PaginatedTraitDatasetRequest setTraitIds(Integer[] traitIds)
	{
		this.traitIds = traitIds;
		return this;
	}
}
