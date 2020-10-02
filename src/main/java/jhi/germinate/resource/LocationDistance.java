package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;

/**
 * @author Sebastian Raubach
 */
public class LocationDistance extends ViewTableLocations
{
	private Double distance;

	public Double getDistance()
	{
		return distance;
	}

	public LocationDistance setDistance(Double distance)
	{
		this.distance = distance;
		return this;
	}
}
