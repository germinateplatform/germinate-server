package jhi.germinate.resource;

public class ServerSetupConfig
{
	private DatabaseConfig dbConfig;
	private GatekeeperConfig gkConfig;

	public DatabaseConfig getDbConfig()
	{
		return dbConfig;
	}

	public ServerSetupConfig setDbConfig(DatabaseConfig dbConfig)
	{
		this.dbConfig = dbConfig;
		return this;
	}

	public GatekeeperConfig getGkConfig()
	{
		return gkConfig;
	}

	public ServerSetupConfig setGkConfig(GatekeeperConfig gkConfig)
	{
		this.gkConfig = gkConfig;
		return this;
	}
}
