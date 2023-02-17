package jhi.germinate.resource;

public class LocaleConfig
{
	private String locale;
	private String name;
	private String flag;

	public String getLocale()
	{
		return locale;
	}

	public LocaleConfig setLocale(String locale)
	{
		this.locale = locale;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public LocaleConfig setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getFlag()
	{
		return flag;
	}

	public LocaleConfig setFlag(String flag)
	{
		this.flag = flag;
		return this;
	}
}
