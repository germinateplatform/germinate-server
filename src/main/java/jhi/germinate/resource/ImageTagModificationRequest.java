package jhi.germinate.resource;

import com.fasterxml.jackson.annotation.JsonGetter;

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

	@JsonGetter("isAddition")
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
