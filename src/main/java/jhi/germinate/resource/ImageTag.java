package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ImageTag
{
	private Integer tagId;
	private String tagName;

	public Integer getTagId()
	{
		return tagId;
	}

	public ImageTag setTagId(Integer tagId)
	{
		this.tagId = tagId;
		return this;
	}

	public String getTagName()
	{
		return tagName;
	}

	public ImageTag setTagName(String tagName)
	{
		this.tagName = tagName;
		return this;
	}
}
