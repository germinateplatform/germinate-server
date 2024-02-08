package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class GermplasmExportRequest extends ExportRequest
{
	private Integer[] individualIds;
	private Integer[] groupIds;
	private Boolean   includeAttributes;

	public Integer[] getIndividualIds()
	{
		return individualIds;
	}

	public GermplasmExportRequest setIndividualIds(Integer[] individualIds)
	{
		this.individualIds = individualIds;
		return this;
	}

	public Integer[] getGroupIds()
	{
		return groupIds;
	}

	public GermplasmExportRequest setGroupIds(Integer[] groupIds)
	{
		this.groupIds = groupIds;
		return this;
	}

	public Boolean getIncludeAttributes()
	{
		return includeAttributes;
	}

	public GermplasmExportRequest setIncludeAttributes(Boolean includeAttributes)
	{
		this.includeAttributes = includeAttributes;
		return this;
	}
}
