package jhi.germinate.resource.enums;

/**
 * @author Sebastian Raubach
 */
public enum ServerProperty
{
	AUTHENTICATION_MODE("authentication.mode", "none", true),
	BCRYPT_SALT("bcrypt.salt", "10", true),
	COLORS_CHART("colors.charts", "#FF9E15,#799900,#00748C,#853175,#555559,#FFD100,#C2002F,#CF009E,#6AA2B8,#D6C200", true),
	COLORS_TEMPLATE("colors.template", "#FF9E15,#799900,#00748C,#853175,#555559,#FFD100,#C2002F,#CF009E,#6AA2B8,#D6C200", true),
	DATA_DIRECTORY_EXTERNAL("data.directory.external", null, true),
	DATABASE_SERVER("database.server", null, true),
	DATABASE_NAME("database.name", null, true),
	DATABASE_USERNAME("database.username", null, true),
	DATABASE_PASSWORD("database.password", null, false),
	DATABASE_PORT("database.port", null, false),
	EXTERNAL_DATA_FOLDER("data.folder.external", null, false),
	GATEKEEPER_URL("gatekeeper.url", null, false),
	GATEKEEPER_USERNAME("gatekeeper.username", null, false),
	GATEKEEPER_PASSWORD("gatekeeper.password", null, false);

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
