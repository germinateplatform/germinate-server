package jhi.germinate.resource;

public class InstitutionUnificationRequest
{
	private Integer      preferredInstitutionId;
	private Integer[]    institutionIds;

	public Integer getPreferredInstitutionId()
	{
		return preferredInstitutionId;
	}

	public InstitutionUnificationRequest setPreferredInstitutionId(Integer preferredInstitutionId)
	{
		this.preferredInstitutionId = preferredInstitutionId;
		return this;
	}

	public Integer[] getInstitutionIds()
	{
		return institutionIds;
	}

	public InstitutionUnificationRequest setInstitutionIds(Integer[] institutionIds)
	{
		this.institutionIds = institutionIds;
		return this;
	}
}
