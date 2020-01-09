package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class NewUserAccessRequest
{
	private String username;
	private String password;
	private String locale;

	public String getUsername()
	{
		return username;
	}

	public NewUserAccessRequest setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public NewUserAccessRequest setPassword(String password)
	{
		this.password = password;
		return this;
	}

	public String getLocale()
	{
		return locale;
	}

	public NewUserAccessRequest setLocale(String locale)
	{
		this.locale = locale;
		return this;
	}
}
