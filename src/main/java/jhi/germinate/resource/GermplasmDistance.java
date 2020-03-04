package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class GermplasmDistance extends ViewTableGermplasm
{
	private Double distance;

	public Double getDistance()
	{
		return distance;
	}

	public GermplasmDistance setDistance(Double distance)
	{
		this.distance = distance;
		return this;
	}
}
