package jhi.germinate.resource;

import jhi.germinate.resource.enums.*;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class ClientConfiguration
{
	private AuthenticationMode authMode;
	private List<String>       colorsTemplate;
	private List<String>       colorsCharts;
	private String             colorPrimary;
	private Boolean            commentsEnabled;
	private List<String>       dashboardCategories;
	private DataImportMode     dataImportMode;
	private String             externalLinkIdentifier;
	private String             externalLinkTemplate;
	private String             googleAnalyticsKey;
	private String             gatekeeperUrl;
	private List<String>       hiddenPages;
	private Boolean            registrationEnabled;
	private Boolean            showGdprNotification;

	public ClientConfiguration()
	{
	}

	public List<String> getColorsTemplate()
	{
		return colorsTemplate;
	}

	public ClientConfiguration setColorsTemplate(List<String> colorsTemplate)
	{
		this.colorsTemplate = colorsTemplate;
		return this;
	}

	public List<String> getColorsCharts()
	{
		return colorsCharts;
	}

	public ClientConfiguration setColorsCharts(List<String> colorsCharts)
	{
		this.colorsCharts = colorsCharts;
		return this;
	}

	public String getColorPrimary()
	{
		return colorPrimary;
	}

	public ClientConfiguration setColorPrimary(String colorPrimary)
	{
		this.colorPrimary = colorPrimary;
		return this;
	}

	public List<String> getHiddenPages()
	{
		return hiddenPages;
	}

	public ClientConfiguration setHiddenPages(List<String> hiddenPages)
	{
		this.hiddenPages = hiddenPages;
		return this;
	}

	public String getGatekeeperUrl()
	{
		return gatekeeperUrl;
	}

	public ClientConfiguration setGatekeeperUrl(String gatekeeperUrl)
	{
		this.gatekeeperUrl = gatekeeperUrl;
		return this;
	}

	public AuthenticationMode getAuthMode()
	{
		return authMode;
	}

	public ClientConfiguration setAuthMode(AuthenticationMode authMode)
	{
		this.authMode = authMode;
		return this;
	}

	public String getExternalLinkIdentifier()
	{
		return externalLinkIdentifier;
	}

	public ClientConfiguration setExternalLinkIdentifier(String externalLinkIdentifier)
	{
		this.externalLinkIdentifier = externalLinkIdentifier;
		return this;
	}

	public String getExternalLinkTemplate()
	{
		return externalLinkTemplate;
	}

	public ClientConfiguration setExternalLinkTemplate(String externalLinkTemplate)
	{
		this.externalLinkTemplate = externalLinkTemplate;
		return this;
	}

	public Boolean getRegistrationEnabled()
	{
		return registrationEnabled;
	}

	public ClientConfiguration setRegistrationEnabled(Boolean registrationEnabled)
	{
		this.registrationEnabled = registrationEnabled;
		return this;
	}

	public Boolean getShowGdprNotification()
	{
		return showGdprNotification;
	}

	public ClientConfiguration setShowGdprNotification(Boolean showGdprNotification)
	{
		this.showGdprNotification = showGdprNotification;
		return this;
	}

	public String getGoogleAnalyticsKey()
	{
		return googleAnalyticsKey;
	}

	public ClientConfiguration setGoogleAnalyticsKey(String googleAnalyticsKey)
	{
		this.googleAnalyticsKey = googleAnalyticsKey;
		return this;
	}

	public Boolean getCommentsEnabled()
	{
		return commentsEnabled;
	}

	public ClientConfiguration setCommentsEnabled(Boolean commentsEnabled)
	{
		this.commentsEnabled = commentsEnabled;
		return this;
	}

	public List<String> getDashboardCategories()
	{
		return dashboardCategories;
	}

	public ClientConfiguration setDashboardCategories(List<String> dashboardCategories)
	{
		this.dashboardCategories = dashboardCategories;
		return this;
	}

	public DataImportMode getDataImportMode()
	{
		return dataImportMode;
	}

	public ClientConfiguration setDataImportMode(DataImportMode dataImportMode)
	{
		this.dataImportMode = dataImportMode;
		return this;
	}
}
