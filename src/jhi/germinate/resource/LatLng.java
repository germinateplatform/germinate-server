package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class LatLng
{
	private Double lat;
	private Double lng;

	public Double getLat()
	{
		return lat;
	}

	public LatLng setLat(Double lat)
	{
		this.lat = lat;
		return this;
	}

	public Double getLng()
	{
		return lng;
	}

	public LatLng setLng(Double lng)
	{
		this.lng = lng;
		return this;
	}
}
