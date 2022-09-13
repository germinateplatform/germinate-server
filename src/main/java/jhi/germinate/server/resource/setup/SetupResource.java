package jhi.germinate.server.resource.setup;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.client.GatekeeperService;
import jhi.gatekeeper.resource.Token;
import jhi.gatekeeper.server.database.tables.pojos.Users;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.util.*;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

@Path("setup")
public class SetupResource
{
	@POST
	@Path("/store")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postSetupConfig(ServerSetupConfig config) {
		Response availability = checkAvailability();

		// This means no configuration is required and we don't accept requests.
		if (availability.getStatus() != 200)
			return availability;

		if (config == null)
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid configuration provided.")
						   .entity("Invalid configuration provided.")
						   .build();
		} else {
			Response resp = postDatabaseConfig(config.getDbConfig());
			if (resp.getStatus() != 200)
				return resp;

			if (config.getGkConfig() != null)
			{
				resp = postGatekeeperConfig(config.getGkConfig());
				if (resp.getStatus() != 200)
					return resp;
			}

			PropertyWatcher.set(ServerProperty.DATABASE_SERVER, config.getDbConfig().getHost());
			PropertyWatcher.set(ServerProperty.DATABASE_NAME, config.getDbConfig().getDatabase());
			PropertyWatcher.set(ServerProperty.DATABASE_PORT, config.getDbConfig().getPort());
			PropertyWatcher.set(ServerProperty.DATABASE_USERNAME, config.getDbConfig().getUsername());
			PropertyWatcher.set(ServerProperty.DATABASE_PASSWORD, config.getDbConfig().getPassword());

			if (config.getGkConfig() != null) {
				PropertyWatcher.set(ServerProperty.GATEKEEPER_URL, config.getGkConfig().getUrl());
				PropertyWatcher.set(ServerProperty.GATEKEEPER_USERNAME, config.getGkConfig().getUsername());
				PropertyWatcher.set(ServerProperty.GATEKEEPER_PASSWORD, config.getGkConfig().getPassword());
			}

			// Invalidate all tokens
			AuthenticationFilter.invalidateAllTokens();

			return Response.ok(PropertyWatcher.storeProperties()).build();
		}
	}

	@GET
	@Path("/check")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSetupCheckAvailable()
	{
		return checkAvailability();
	}

	private Response checkAvailability()
	{
		try (Connection conn = Database.getConnection())
		{
			AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

			if (mode != AuthenticationMode.NONE)
			{
				// Check Gatekeeper config
				if (GatekeeperClient.connectionValid())
				{
					// If it valid, no setup is required => SERVICE_UNAVAILABLE
					return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), "Germinate has already been configured successfully.")
								   .entity("Germinate has already been configured successfully.")
								   .build();
				}
				else
				{
					// If it's not valid, setup is required => OK
					return Response.ok().build();
				}
			}
			else
			{
				// Database connection works, no Gatekeeper required => SERVICE_UNAVAILABLE
				return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), "Germinate has already been configured successfully.")
							   .entity("Germinate has already been configured successfully.")
							   .build();
			}
		}
		catch (SQLException e)
		{
			// If we get an exception, that means configuration is still required
			return Response.ok().build();
		}
	}

	@POST
	@Path("/check/database")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postDatabaseConfig(DatabaseConfig config)
	{
		Response availability = checkAvailability();

		// This means no configuration is required and we don't accept requests.
		if (availability.getStatus() != 200)
			return availability;

		if (config == null || StringUtils.isEmpty(config.getHost()) || StringUtils.isEmpty(config.getDatabase()) || StringUtils.isEmpty(config.getUsername()))
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid database configuration provided. Please complete at least Host, Database and username fields.")
						   .entity("Invalid database configuration provided. Please complete at least Host, Database and username fields.")
						   .build();
		}

		boolean valid = Database.check(config.getHost(), config.getDatabase(), config.getPort(), config.getUsername(), config.getPassword());

		if (valid)
			return Response.ok().build();
		else
			return Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid database details provided.")
						   .entity("Invalid database details provided.")
						   .build();
	}

	@POST
	@Path("/check/gatekeeper")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postGatekeeperConfig(GatekeeperConfig config)
	{
		Response availability = checkAvailability();

		// This means no configuration is required and we don't accept requests.
		if (availability.getStatus() != 200)
			return availability;

		if (config == null || StringUtils.isEmpty(config.getUrl()) || StringUtils.isEmpty(config.getUsername()))
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid Gatekeeper configuration provided. Please complete at least URL and username fields.")
						   .entity("Invalid Gatekeeper configuration provided. Please complete at least URL and username fields.")
						   .build();
		}

		// Create the HTTP client with the pool and timeouts
		OkHttpClient httpClient = new OkHttpClient.Builder()
			.readTimeout(20, TimeUnit.SECONDS)
			.callTimeout(20, TimeUnit.SECONDS)
			.connectTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.retryOnConnectionFailure(true)
			.build();

		String url = config.getUrl();
		// Fix any issues that might occur with the URL
		if (!url.endsWith("/"))
			url += "/";

		if (!url.endsWith("api/"))
			url += "api/";

		// Create the retrofit instance
		Retrofit retrofit = (new Retrofit.Builder()).baseUrl(url)
													.addConverterFactory(GsonConverterFactory.create())
													.client(httpClient)
													.build();

		// Create an instance of the service interface
		GatekeeperService service = retrofit.create(GatekeeperService.class);

		Users users = new Users();
		users.setUsername(config.getUsername());
		users.setPassword(config.getPassword());

		try
		{
			retrofit2.Response<Token> resp = service.postToken(users).execute();

			if (!resp.isSuccessful())
			{
				if (resp.code() == 500)
				{
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal server error: " + resp.message())
								   .entity("Internal server error: " + resp.message())
								   .build();
				}
				else
				{
					return Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid details: " + resp.message())
								   .entity("Invalid details: " + resp.message())
								   .build();
				}
			}
			else
			{
				return Response.ok().build();
			}
		}
		catch (IOException e)
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid Gatekeeper URL specfied: " + e.getMessage())
						   .entity("Invalid Gatekeeper URL specfied: " + e.getMessage())
						   .build();
		}
		finally
		{
			if (!httpClient.dispatcher().executorService().isTerminated())
			{
				try
				{
					httpClient.dispatcher().executorService().shutdown();
					httpClient.connectionPool().evictAll();
					httpClient.cache().close();
				}
				catch (Exception e)
				{
					// Ignore exceptions here
				}
			}
		}
	}
}
