package jhi.germinate.resource;

import java.util.List;

public class GenesysRequestDetails
{
	private String        name;
	private String        email;
	private List<Integer> germplasmIds;

	public GenesysRequestDetails()
	{
	}

	public String getName()
	{
		return name;
	}

	public GenesysRequestDetails setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getEmail()
	{
		return email;
	}

	public GenesysRequestDetails setEmail(String email)
	{
		this.email = email;
		return this;
	}

	public List<Integer> getGermplasmIds()
	{
		return germplasmIds;
	}

	public GenesysRequestDetails setGermplasmIds(List<Integer> germplasmIds)
	{
		this.germplasmIds = germplasmIds;
		return this;
	}
}
