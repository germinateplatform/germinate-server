package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DatasetLocation
{
	private Integer locationId;
	private String locationName;
	private Integer countryId;
	private String countryName;

	public Integer getLocationId()
	{
		return locationId;
	}

	public DatasetLocation setLocationId(Integer locationId)
	{
		this.locationId = locationId;
		return this;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public DatasetLocation setLocationName(String locationName)
	{
		this.locationName = locationName;
		return this;
	}

	public Integer getCountryId()
	{
		return countryId;
	}

	public DatasetLocation setCountryId(Integer countryId)
	{
		this.countryId = countryId;
		return this;
	}

	public String getCountryName()
	{
		return countryName;
	}

	public DatasetLocation setCountryName(String countryName)
	{
		this.countryName = countryName;
		return this;
	}
}
