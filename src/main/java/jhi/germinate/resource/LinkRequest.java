package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class LinkRequest
{
	private String targetTable;
	private Integer foreignId;

	public String getTargetTable()
	{
		return targetTable;
	}

	public LinkRequest setTargetTable(String targetTable)
	{
		this.targetTable = targetTable;
		return this;
	}

	public Integer getForeignId()
	{
		return foreignId;
	}

	public LinkRequest setForeignId(Integer foreignId)
	{
		this.foreignId = foreignId;
		return this;
	}
}
