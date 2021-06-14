package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class LoginDetails
{
	private String username;
	private String password;

	public LoginDetails()
	{
	}

	public LoginDetails(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public LoginDetails setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public LoginDetails setPassword(String password)
	{
		this.password = password;
		return this;
	}
}
