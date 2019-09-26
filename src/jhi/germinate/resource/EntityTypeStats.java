package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class EntityTypeStats
{
	private Integer entityTypeId;
	private String entityTypeName;
	private Integer count;

	public EntityTypeStats()
	{
	}

	public Integer getEntityTypeId()
	{
		return entityTypeId;
	}

	public EntityTypeStats setEntityTypeId(Integer entityTypeId)
	{
		this.entityTypeId = entityTypeId;
		return this;
	}

	public String getEntityTypeName()
	{
		return entityTypeName;
	}

	public EntityTypeStats setEntityTypeName(String entityTypeName)
	{
		this.entityTypeName = entityTypeName;
		return this;
	}

	public Integer getCount()
	{
		return count;
	}

	public EntityTypeStats setCount(Integer count)
	{
		this.count = count;
		return this;
	}
}
