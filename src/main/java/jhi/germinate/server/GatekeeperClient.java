package jhi.germinate.server;

import jhi.gatekeeper.client.GatekeeperService;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.util.*;
import okhttp3.*;
import retrofit2.Response;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperClient
{
	private static GatekeeperService service;

	private static String url;
	private static String username;
	private static String password;
	private static Token  token;

	private static final ConcurrentHashMap<Integer, ViewUserDetails> users = new ConcurrentHashMap<>();
	private static       Retrofit                                    retrofit;
	private static       OkHttpClient                                httpClient;
	private static       ConnectionPool                              connectionPool;

	/**
	 * Initialises the Gatekeeper Client using the remote URL, username and password
	 * @param url The remote API URL of Gatekeeper
	 * @param username The username to use. This has to be a user with admin permissions on the Gatekeeper database.
	 * @param password The user password.
	 */
	public static void init(String url, String username, String password)
	{
		Logger.getLogger("").info("GATEKEEPER CLIENT INIT: " + url + " " + username + " " + password);

		if (PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class) == AuthenticationMode.NONE
			|| StringUtils.isEmpty(url)
			|| StringUtils.isEmpty(username))
		{
			return;
		}

		// Fix any issues that might occur with the URL
		if (!url.endsWith("/"))
			url += "/";

		if (!url.endsWith("api/"))
			url += "api/";

		GatekeeperClient.url = url;
		GatekeeperClient.username = username;
		GatekeeperClient.password = password;

		// Create a connection pool
		connectionPool = new ConnectionPool(3, 1, TimeUnit.MINUTES);

		try
		{
			// (Re)set everything
			reset();
		}
		catch (Exception e)
		{
			Logger.getLogger("").info(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void reset()
	{
		// Close any existing connections
		close();

		if (connectionPool == null) {
			// Create a connection pool
			connectionPool = new ConnectionPool(3, 1, TimeUnit.MINUTES);
		}

		Logger.getLogger("").info("GATEKEEPER CLIENT RESET: " + url);

		// Create the HTTP client with the pool and timeouts
		httpClient = new OkHttpClient.Builder()
			.readTimeout(20, TimeUnit.SECONDS)
			.callTimeout(20, TimeUnit.SECONDS)
			.connectTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.connectionPool(connectionPool)
			.retryOnConnectionFailure(true)
			.build();
		// Create the retrofit instance
		retrofit = (new Retrofit.Builder()).baseUrl(url)
										   .addConverterFactory(GsonConverterFactory.create())
										   .client(httpClient)
										   .build();

		// Create an instance of the service interface
		service = retrofit.create(GatekeeperService.class);

		// Create a user POJO
		Users user = new Users();
		user.setUsername(username);
		user.setPassword(password);
		try
		{
			// Log in using the credentials
			Response<Token> response = service.postToken(user).execute();

			// Remember the token if it worked
			if (response.isSuccessful())
				token = response.body();
			else
				throw new RuntimeException(response.errorBody().string());
		}
		catch (IOException e)
		{
			Logger.getLogger("").info(e.getMessage());
			e.printStackTrace();
			return;
		}

		// Close the current connection again
		close();
		// And now create a new one that will use the token for all further requests
		httpClient = (new OkHttpClient.Builder())
			.readTimeout(20, TimeUnit.SECONDS)
			.callTimeout(20, TimeUnit.SECONDS)
			.connectTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.connectionPool(connectionPool)
			.retryOnConnectionFailure(true)
			.addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
														.addHeader("Authorization", "Bearer " + token.getToken())
														.addHeader("Cookie", "token=" + token.getToken())
														.build()))
			.addInterceptor(chain -> {
				// Add an interceptor that will handle some error codes
				Request request = chain.request();
				okhttp3.Response response = chain.proceed(request);

				// On any 403, reset the client. Gatekeeper may have restarted and the token would then have been removed.
				if (response.code() == 403)
				{
					response.close();
					// Reset the client
					GatekeeperClient.reset();
					// Then send the request again
					return chain.proceed(request);
				}

				return response;
			})
			.build();

		// Now recreate the retrofit instance and the service
		retrofit = (new Retrofit.Builder()).baseUrl(url)
										   .addConverterFactory(GsonConverterFactory.create())
										   .client(httpClient)
										   .build();
		service = retrofit.create(GatekeeperService.class);
	}

	public static synchronized GatekeeperService get()
	{
		if (token == null || token.getCreatedOn() + token.getLifetime() < System.currentTimeMillis())
			reset();

		return service;
	}

	/**
	 * Gets all users with access to this Germinate database from Gatekeeper
	 */
	public static synchronized void getUsersFromGatekeeper()
	{
		try
		{
			// Each time the client is reset, get users from Gatekeeper
			PaginatedResult<List<ViewUserDetails>> allUsers = service.getUsers(Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute().body();

			if (allUsers != null)
			{
				GatekeeperClient.users.clear();
				allUsers.getData().forEach(u -> GatekeeperClient.users.put(u.getId(), u));
			}
		}
		catch (IOException e)
		{
			Logger.getLogger("").info(e.getMessage());
			e.printStackTrace();
		}
	}

	public static GatekeeperApiError parseError(Response<?> response)
	{
		Converter<ResponseBody, GatekeeperApiError> converter = retrofit.responseBodyConverter(GatekeeperApiError.class, new Annotation[0]);

		GatekeeperApiError error;

		try
		{
			error = converter.convert(response.errorBody());
		}
		catch (Exception e)
		{
			Logger.getLogger("").info(e.getMessage());
			return new GatekeeperApiError();
		}

		return error;
	}

	/**
	 * Returns the Gatekeeper user with the given id if any. <code>null</code> otherwise
	 * @param id The id of the user
	 * @return The user or <code>null</code>
	 */
	public static ViewUserDetails getUser(Integer id)
	{
		if (id == null)
		{
			return null;
		}
		else if (users.size() < 1)
		{
			if (PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class) != AuthenticationMode.NONE)
			{
				// Get the user
				ViewUserDetails result = users.get(id);

				// If it doesn't exist, try to get it again from Gatekeeper
				if (result == null)
				{
					// Try to get them again, Gatekeeper may have been unavailable
					getUsersFromGatekeeper();
					result = users.get(id);
				}

				return result;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return users.get(id);
		}
	}

	public static List<ViewUserDetails> getUsers()
	{
		return new ArrayList<>(users.values());
	}

	public static void close()
	{
		if (httpClient != null && !httpClient.dispatcher().executorService().isTerminated())
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
