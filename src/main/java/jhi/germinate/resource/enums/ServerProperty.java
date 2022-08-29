package jhi.germinate.resource.enums;

/**
 * @author Sebastian Raubach
 */
public enum ServerProperty
{
	AUTHENTICATION_MODE("authentication.mode", "NONE", true),
	BCRYPT_SALT("bcrypt.salt", "10", true),
	BRAPI_ENABLED("brapi.enabled", "false", false),
	COLORS_CHART("colors.charts", "#00a0f1,#5ec418,#910080,#222183,#ff7c00,#c5e000,#c83831,#ff007a,#fff600", true),
	COLORS_TEMPLATE("colors.template", "#FF9E15,#799900,#00748C,#853175,#555559,#FFD100,#C2002F,#CF009E,#6AA2B8,#D6C200", true),
	COLORS_GRADIENT("colors.gradient", "#440154,#48186a,#472d7b,#424086,#3b528b,#33638d,#2c728e,#26828e,#21918c,#1fa088,#28ae80,#3fbc73,#5ec962,#84d44b,#addc30,#d8e219,#fde725", true),
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
	EXTERNAL_LINK_IDENTIFIER("external.link.identifier", null, false),
	EXTERNAL_LINK_TEMPLATE("external.link.template", null, false),
	GERMINATE_CLIENT_URL("germinate.client.url", null, true),
	GATEKEEPER_URL("gatekeeper.url", null, false),
	GATEKEEPER_USERNAME("gatekeeper.username", null, false),
	GATEKEEPER_PASSWORD("gatekeeper.password", null, false),
	GATEKEEPER_REGISTRATION_ENABLED("gatekeeper.registration.enabled", "false", false),
	GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL("gatekeeper.registration.requires.approval", "true", false),
	GOOGLE_ANALYTICS_KEY("google.analytics.key", null, false),
	PLAUSIBLE_DOMAIN("plausible.domain", null, false),
	PLAUSIBLE_HASH_MODE("plausible.hash.mode", "true", false),
	PLAUSIBLE_API_HOST("plausible.api.host", "https://plausible.io", false),
	HELIUM_URL("helium.url", null, false),
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
