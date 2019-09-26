package jhi.germinate.resource;

import jhi.germinate.server.database.tables.pojos.ViewTableAttributes;

/**
 * @author Sebastian Raubach
 */
public class GermplasmAttributeData extends ViewTableAttributes
{
	private Integer germplasmId;
	private String germplasmGid;
	private String germplasmName;

	public Integer getGermplasmId()
	{
		return germplasmId;
	}

	public GermplasmAttributeData setGermplasmId(Integer germplasmId)
	{
		this.germplasmId = germplasmId;
		return this;
	}

	public String getGermplasmGid()
	{
		return germplasmGid;
	}

	public GermplasmAttributeData setGermplasmGid(String germplasmGid)
	{
		this.germplasmGid = germplasmGid;
		return this;
	}

	public String getGermplasmName()
	{
		return germplasmName;
	}

	public GermplasmAttributeData setGermplasmName(String germplasmName)
	{
		this.germplasmName = germplasmName;
		return this;
	}
}
