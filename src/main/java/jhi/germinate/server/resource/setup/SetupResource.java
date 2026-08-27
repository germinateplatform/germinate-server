package jhi.germinate.server.resource.setup;

import jakarta.ws.rs.Path;

@Path("setup")
public class SetupResource
{
//	@POST
//	@Path("/store")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public boolean postSetupConfig(ServerSetupConfig config)
//	{
//		checkAvailability();
//
//		if (config == null)
//			throw new BadRequestException("Invalid configuration provided.");
//		else
//		{
//			boolean db = postDatabaseConfig(config.getDbConfig());
//			if (!db)
//				return false;
//
//			if (config.getGkConfig() != null)
//			{
//				boolean gk = postGatekeeperConfig(config.getGkConfig());
//				if (!gk)
//					return false;
//			}
//
//			PropertyWatcher.set(ServerProperty.DATABASE_SERVER, config.getDbConfig().getHost());
//			PropertyWatcher.set(ServerProperty.DATABASE_NAME, config.getDbConfig().getDatabase());
//			PropertyWatcher.set(ServerProperty.DATABASE_PORT, config.getDbConfig().getPort());
//			PropertyWatcher.set(ServerProperty.DATABASE_USERNAME, config.getDbConfig().getUsername());
//			PropertyWatcher.set(ServerProperty.DATABASE_PASSWORD, config.getDbConfig().getPassword());
//
//			if (config.getGkConfig() != null)
//			{
//				PropertyWatcher.set(ServerProperty.GATEKEEPER_URL, config.getGkConfig().getUrl());
//				PropertyWatcher.set(ServerProperty.GATEKEEPER_USERNAME, config.getGkConfig().getUsername());
//				PropertyWatcher.set(ServerProperty.GATEKEEPER_PASSWORD, config.getGkConfig().getPassword());
//			}
//
//			// Invalidate all tokens
//			AuthenticationFilter.invalidateAllTokens();
//
//			return PropertyWatcher.storeProperties();
//		}
//	}
//
//	@GET
//	@Path("/check")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public boolean getSetupCheckAvailable()
//	{
//		return checkAvailability();
//	}
//
//	private boolean checkAvailability()
//	{
//		try (Connection conn = Database.getConnection())
//		{
//			AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
//
//			if (mode != AuthenticationMode.NONE)
//			{
//				// Check Gatekeeper config
//				if (GatekeeperClient.connectionValid())
//				{
//					// If it valid, no setup is required => SERVICE_UNAVAILABLE
//					throw new ServiceUnavailableException("Germinate has already been configured successfully.");
//				}
//				else
//				{
//					// If it's not valid, setup is required => OK
//					return true;
//				}
//			}
//			else
//			{
//				// Database connection works, no Gatekeeper required => SERVICE_UNAVAILABLE
//				throw new ServiceUnavailableException("Germinate has already been configured successfully.");
//			}
//		}
//		catch (SQLException e)
//		{
//			// If we get an exception, that means configuration is still required
//			return false;
//		}
//	}
//
//	@POST
//	@Path("/check/database")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public boolean postDatabaseConfig(DatabaseConfig config)
//	{
//		checkAvailability();
//
//		if (config == null || StringUtils.isEmpty(config.getHost()) || StringUtils.isEmpty(config.getDatabase()) || StringUtils.isEmpty(config.getUsername()))
//			throw new BadRequestException("Invalid database configuration provided. Please complete at least Host, Database and username fields.");
//
//		boolean valid = Database.check(config.getHost(), config.getDatabase(), config.getPort(), config.getUsername(), config.getPassword());
//
//		if (valid)
//			return true;
//		else
//			throw new StatusException(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid database details provided.");
//	}
//
//	@POST
//	@Path("/check/gatekeeper")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public boolean postGatekeeperConfig(GatekeeperConfig config)
//	{
//		checkAvailability();
//
//		if (config == null || StringUtils.isEmpty(config.getUrl()) || StringUtils.isEmpty(config.getUsername()))
//			throw new BadRequestException("Invalid Gatekeeper configuration provided. Please complete at least URL and username fields.");
//
//		// Create the HTTP client with the pool and timeouts
//		OkHttpClient httpClient = new OkHttpClient.Builder()
//				.readTimeout(20, TimeUnit.SECONDS)
//				.callTimeout(20, TimeUnit.SECONDS)
//				.connectTimeout(20, TimeUnit.SECONDS)
//				.writeTimeout(20, TimeUnit.SECONDS)
//				.retryOnConnectionFailure(true)
//				.build();
//
//		String url = config.getUrl();
//		// Fix any issues that might occur with the URL
//		if (!url.endsWith("/"))
//			url += "/";
//
//		if (!url.endsWith("api/"))
//			url += "api/";
//
//		// Create the retrofit instance
//		Retrofit retrofit = (new Retrofit.Builder()).baseUrl(url)
//		                                            .addConverterFactory(GsonConverterFactory.create())
//		                                            .client(httpClient)
//		                                            .build();
//
//		// Create an instance of the service interface
//		GatekeeperService service = retrofit.create(GatekeeperService.class);
//
//		Users users = new Users();
//		users.setUsername(config.getUsername());
//		users.setPassword(config.getPassword());
//
//		try
//		{
//			retrofit2.Response<Token> resp = service.postToken(users).execute();
//
//			if (!resp.isSuccessful())
//			{
//				if (resp.code() == 500)
//					throw new InternalServerErrorException("Internal server error: " + resp.message());
//				else
//					throw new StatusException(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid details: " + resp.message());
//			}
//			else
//			{
//				return true;
//			}
//		}
//		catch (IOException e)
//		{
//			throw new BadRequestException("Invalid Gatekeeper URL specfied: " + e.getMessage());
//		}
//		finally
//		{
//			if (!httpClient.dispatcher().executorService().isTerminated())
//			{
//				try
//				{
//					httpClient.dispatcher().executorService().shutdown();
//					httpClient.connectionPool().evictAll();
//					httpClient.cache().close();
//				}
//				catch (Exception e)
//				{
//					// Ignore exceptions here
//				}
//			}
//		}
//	}
}
