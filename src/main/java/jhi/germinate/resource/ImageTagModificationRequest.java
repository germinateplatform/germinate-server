package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ImageTagModificationRequest
{
	private String[] tags;
	private boolean  isAddition;

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

	public boolean isAddition()
	{
		return isAddition;
	}

	public ImageTagModificationRequest setAddition(boolean addition)
	{
		isAddition = addition;
		return this;
	}
}
