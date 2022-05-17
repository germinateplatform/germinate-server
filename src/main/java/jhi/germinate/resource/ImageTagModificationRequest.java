package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ImageTagModificationRequest
{
	private String[] tags;
	private Boolean  addition;

	public ImageTagModificationRequest()
	{
	}

	public String[] getTags()
	{
		return tags;
	}

	public ImageTagModificationRequest setTags(String[] tags)
	{
		this.tags = tags;
		return this;
	}

	public Boolean getAddition()
	{
		return addition;
	}

	public ImageTagModificationRequest setAddition(Boolean addition)
	{
		this.addition = addition;
		return this;
	}
}
