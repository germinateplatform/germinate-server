package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ViewTablePublicationGermplasm extends ViewTableGermplasm
{
	private Integer publicationId;

	public Integer getPublicationId()
	{
		return publicationId;
	}

	public ViewTablePublicationGermplasm setPublicationId(Integer publicationId)
	{
		this.publicationId = publicationId;
		return this;
	}
}
