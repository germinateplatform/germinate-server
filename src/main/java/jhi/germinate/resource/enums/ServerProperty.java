package jhi.germinate.resource.enums;

/**
 * @author Sebastian Raubach
 */
public enum ServerProperty
{
	AUTHENTICATION_MODE("authentication.mode", "none", true),
	BCRYPT_SALT("bcrypt.salt", "10", true),
	COLORS_CHART("colors.charts", "#1f77b4,#ff7f0e,#2ca02c,#d62728,#9467bd,#8c564b,#e377c2,#7f7f7f,#bcbd22,#17becf", true),
	COLORS_TEMPLATE("colors.template", "#FF9E15,#799900,#00748C,#853175,#555559,#FFD100,#C2002F,#CF009E,#6AA2B8,#D6C200", true),
	COLOR_PRIMARY("color.primary", null, false),
	COMMENTS_ENABLED("comments.enabled", "true", false),
	DASHBOARD_CATEGORIES("dashboard.categories", "germplasm,markers,traits,locations", false),
	DATA_DIRECTORY_EXTERNAL("data.directory.external", null, true),
	DATA_IMPORT_MODE("data.import.mode", "NONE", false),
	DATABASE_SERVER("database.server", null, true),
	DATABASE_NAME("database.name", null, true),
	DATABASE_USERNAME("database.username", null, true),
	DATABASE_PASSWORD("database.password", null, false),
	DATABASE_PORT("database.port", null, false),
	GERMINATE_CLIENT_URL("germinate.client.url", null, true),
	GATEKEEPER_URL("gatekeeper.url", null, false),
	GATEKEEPER_USERNAME("gatekeeper.username", null, false),
	GATEKEEPER_PASSWORD("gatekeeper.password", null, false),
	GATEKEEPER_REGISTRATION_ENABLED("gatekeeper.registration.enabled", "false", false),
	GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL("gatekeeper.registration.requires.approval", "true", false),
	GOOGLE_ANALYTICS_KEY("google.analytics.key", null, false),
	GRPD_NOTIFICATION_ENABLED("gdpr.notification.enabled", "false", false),
	HIDDEN_PAGES("hidden.pages", null, false),
	PDCI_ENABLED("pdci.enabled", "true", false),
	FILES_DELETE_AFTER_HOURS_ASYNC("files.delete.after.hours.async", "12", false),
	FILES_DELETE_AFTER_HOURS_TEMP("files.delete.after.hours.temp", "12", false);

	String  key;
	String  defaultValue;
	boolean required;

	ServerProperty(String key, String defaultValue, boolean required)
	{
		this.key = key;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public String getKey()
	{
		return key;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public boolean isRequired()
	{
		return required;
	}
}
