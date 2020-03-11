package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class UuidRequest
{
	private String[] uuids;

	public String[] getUuids()
	{
		return uuids;
	}

	public UuidRequest setUuids(String[] uuids)
	{
		this.uuids = uuids;
		return this;
	}
}
