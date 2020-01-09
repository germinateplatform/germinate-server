package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class UnacceptedLicenseRequest extends PaginatedRequest
{
	private Boolean justUnacceptedLicenses;

	public Boolean getJustUnacceptedLicenses()
	{
		return justUnacceptedLicenses;
	}

	public UnacceptedLicenseRequest setJustUnacceptedLicenses(Boolean justUnacceptedLicenses)
	{
		this.justUnacceptedLicenses = justUnacceptedLicenses;
		return this;
	}
}
