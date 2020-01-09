package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetAsyncJobRequest
{
	private String[] uuids;

	public String[] getUuids()
	{
		return uuids;
	}

	public DatasetAsyncJobRequest setUuids(String[] uuids)
	{
		this.uuids = uuids;
		return this;
	}
}
