package jhi.germinate.resource;

public class DatabaseConfig
{
	private String host;
	private String database;
	private String port;
	private String username;
	private String password;

	public String getHost()
	{
		return host;
	}

	public DatabaseConfig setHost(String host)
	{
		this.host = host;
		return this;
	}

	public String getDatabase()
	{
		return database;
	}

	public DatabaseConfig setDatabase(String database)
	{
		this.database = database;
		return this;
	}

	public String getPort()
	{
		return port;
	}

	public DatabaseConfig setPort(String port)
	{
		this.port = port;
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public DatabaseConfig setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public DatabaseConfig setPassword(String password)
	{
		this.password = password;
		return this;
	}
}
