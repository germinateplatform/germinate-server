package jhi.germinate.resource;

public class GatekeeperConfig
{
	private String url;
	private String username;
	private String password;

	public String getUrl()
	{
		return url;
	}

	public GatekeeperConfig setUrl(String url)
	{
		this.url = url;
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public GatekeeperConfig setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public GatekeeperConfig setPassword(String password)
	{
		this.password = password;
		return this;
	}
}
