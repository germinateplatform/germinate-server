package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ClientAdminConfiguration extends ClientConfiguration
{
	private Integer bcryptSalt;
	private Boolean brapiEnabled;
	private String  dataDirectoryExternal;
	private String  gatekeeperUsername;
	private String  gatekeeperPassword;
	private Boolean gatekeeperRegistrationRequiresApproval;
	private Boolean pdciEnabled;
	private Integer filesDeleteAfterHoursAsync;
	private Integer filesDeleteAfterHoursTemp;
	private Boolean hiddenPagesAutodiscover;

	public Integer getBcryptSalt()
	{
		return bcryptSalt;
	}

	public ClientAdminConfiguration setBcryptSalt(Integer bcryptSalt)
	{
		this.bcryptSalt = bcryptSalt;
		return this;
	}

	public Boolean getBrapiEnabled()
	{
		return brapiEnabled;
	}

	public ClientAdminConfiguration setBrapiEnabled(Boolean brapiEnabled)
	{
		this.brapiEnabled = brapiEnabled;
		return this;
	}

	public String getDataDirectoryExternal()
	{
		return dataDirectoryExternal;
	}

	public ClientAdminConfiguration setDataDirectoryExternal(String dataDirectoryExternal)
	{
		this.dataDirectoryExternal = dataDirectoryExternal;
		return this;
	}

	public String getGatekeeperUsername()
	{
		return gatekeeperUsername;
	}

	public ClientAdminConfiguration setGatekeeperUsername(String gatekeeperUsername)
	{
		this.gatekeeperUsername = gatekeeperUsername;
		return this;
	}

	public String getGatekeeperPassword()
	{
		return gatekeeperPassword;
	}

	public ClientAdminConfiguration setGatekeeperPassword(String gatekeeperPassword)
	{
		this.gatekeeperPassword = gatekeeperPassword;
		return this;
	}

	public Boolean getGatekeeperRegistrationRequiresApproval()
	{
		return gatekeeperRegistrationRequiresApproval;
	}

	public ClientAdminConfiguration setGatekeeperRegistrationRequiresApproval(Boolean gatekeeperRegistrationRequiresApproval)
	{
		this.gatekeeperRegistrationRequiresApproval = gatekeeperRegistrationRequiresApproval;
		return this;
	}

	public Boolean getPdciEnabled()
	{
		return pdciEnabled;
	}

	public ClientAdminConfiguration setPdciEnabled(Boolean pdciEnabled)
	{
		this.pdciEnabled = pdciEnabled;
		return this;
	}

	public Integer getFilesDeleteAfterHoursAsync()
	{
		return filesDeleteAfterHoursAsync;
	}

	public ClientAdminConfiguration setFilesDeleteAfterHoursAsync(Integer filesDeleteAfterHoursAsync)
	{
		this.filesDeleteAfterHoursAsync = filesDeleteAfterHoursAsync;
		return this;
	}

	public Integer getFilesDeleteAfterHoursTemp()
	{
		return filesDeleteAfterHoursTemp;
	}

	public ClientAdminConfiguration setFilesDeleteAfterHoursTemp(Integer filesDeleteAfterHoursTemp)
	{
		this.filesDeleteAfterHoursTemp = filesDeleteAfterHoursTemp;
		return this;
	}

	public Boolean getHiddenPagesAutodiscover()
	{
		return hiddenPagesAutodiscover;
	}

	public ClientAdminConfiguration setHiddenPagesAutodiscover(Boolean hiddenPagesAutodiscover)
	{
		this.hiddenPagesAutodiscover = hiddenPagesAutodiscover;
		return this;
	}
}
