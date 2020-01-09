package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PaginatedLocationRequest extends PaginatedRequest
{
	private Double latitude;
	private Double longitude;

	public Double getLatitude()
	{
		return latitude;
	}

	public PaginatedLocationRequest setLatitude(Double latitude)
	{
		this.latitude = latitude;
		return this;
	}

	public Double getLongitude()
	{
		return longitude;
	}

	public PaginatedLocationRequest setLongitude(Double longitude)
	{
		this.longitude = longitude;
		return this;
	}
}
