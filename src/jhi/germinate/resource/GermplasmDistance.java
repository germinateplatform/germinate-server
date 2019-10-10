package jhi.germinate.resource;

import jhi.germinate.server.database.tables.pojos.ViewTableGermplasm;

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
