package jhi.germinate.server.resource.genesys;

import java.util.*;

public class GenesysResponse
{
	private String                                            uuid;
	private List<GenesysGermplasmResource.GenesysRequestItem> missingItems;

	public GenesysResponse()
	{
	}

	public String getUuid()
	{
		return uuid;
	}

	public GenesysResponse setUuid(String uuid)
	{
		this.uuid = uuid;
		return this;
	}

	public List<GenesysGermplasmResource.GenesysRequestItem> getMissingItems()
	{
		return missingItems;
	}

	public GenesysResponse setMissingItems(List<GenesysGermplasmResource.GenesysRequestItem> missingItems)
	{
		this.missingItems = missingItems;
		return this;
	}

	@Override
	public String toString()
	{
		return "GenesysResponse{" +
				"uuid='" + uuid + '\'' +
				", missingItems=" + missingItems +
				'}';
	}
}
