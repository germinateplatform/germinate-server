package jhi.germinate.server.resource.genesys;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.ws.rs.ServiceUnavailableException;
import okhttp3.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GenesysClient
{
	private static GenesysService service;

	private static String url;
	private static String clientId;
	private static String clientSecret;
	private static String token;

	private static Retrofit       retrofit;
	private static OkHttpClient   httpClient;
	private static ConnectionPool connectionPool;

	/**
	 * Initialises the Gatekeeper Client using the remote URL, username and password
	 *
	 * @param url          The remote API URL of Gatekeeper
	 * @param clientId     The username to use. This has to be a user with admin permissions on the Gatekeeper database.
	 * @param clientSecret The user password.
	 */
	public static void init(String url, String clientId, String clientSecret)
	{
		GenesysClient.url = url;
		GenesysClient.clientId = clientId;
		GenesysClient.clientSecret = clientSecret;

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
		token = null;

		// Close any existing connections
		close();

		if (url == null)
		{
			return;
		}

		if (connectionPool == null)
		{
			// Create a connection pool
			connectionPool = new ConnectionPool(3, 1, TimeUnit.MINUTES);
		}

		// Create the HTTP client with the pool and timeouts
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.readTimeout(20, TimeUnit.SECONDS)
				.callTimeout(20, TimeUnit.SECONDS)
				.connectTimeout(20, TimeUnit.SECONDS)
				.writeTimeout(20, TimeUnit.SECONDS)
				.connectionPool(connectionPool)
				.retryOnConnectionFailure(true);

		httpClient = builder.build();
		// Create the retrofit instance
		retrofit = (new Retrofit.Builder()).baseUrl(url)
										   .addConverterFactory(GsonConverterFactory.create())
										   .client(httpClient)
										   .build();

		// Create an instance of the service interface
		service = retrofit.create(GenesysService.class);

		// Create a user POJO
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("grant_type", "client_credentials")
				.addFormDataPart("client_id", clientId)
				.addFormDataPart("client_secret", clientSecret)
				.build();
		try
		{
			// Log in using the credentials
			Response<GenesysToken> response = service.postToken(requestBody).execute();

			// Remember the token if it worked
			if (response.isSuccessful())
				token = response.body().getAccessToken();
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
		builder = new OkHttpClient.Builder()
				.readTimeout(20, TimeUnit.SECONDS)
				.callTimeout(20, TimeUnit.SECONDS)
				.connectTimeout(20, TimeUnit.SECONDS)
				.writeTimeout(20, TimeUnit.SECONDS)
				.connectionPool(connectionPool)
				.retryOnConnectionFailure(true)
				.addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
															.addHeader("Authorization", "Bearer " + token)
															.build()))
				.addInterceptor(chain -> {
					// Add an interceptor that will handle some error codes
					Request request = chain.request();
					okhttp3.Response response = chain.proceed(request);

					// On any 403, reset the client. Gatekeeper may have restarted and the token would then have been removed.
					if (response.code() == 401)
					{
						response.close();
						// Reset the client
						GenesysClient.reset();
						// Then send the request again
						return chain.proceed(request);
					}

					return response;
				});

		httpClient = builder
				.build();

		// Now recreate the retrofit instance and the service
		retrofit = (new Retrofit.Builder()).baseUrl(url)
										   .addConverterFactory(GsonConverterFactory.create())
										   .client(httpClient)
										   .build();
		service = retrofit.create(GenesysService.class);
	}

	/**
	 * Gets all users with access to this Germinate database from Gatekeeper
	 *
	 * @return
	 */
	public static GenesysResponse postGermplasmRequest(GenesysGermplasmResource.GenesysRequest request)
			throws ServiceUnavailableException
	{
		if (service == null)
			throw new ServiceUnavailableException();
		try
		{
			Response<String> response = service.postGermplasm(request).execute();

			if (response.isSuccessful())
			{
				return new GenesysResponse().setUuid(response.body());
			}
			else
			{
				Gson gson = new GsonBuilder()
						.create();

				// It is an array, parse the data
				Type responseType = new TypeToken<List<GenesysGermplasmResource.GenesysRequestItem>>()
				{
				}.getType();
				List<GenesysGermplasmResource.GenesysRequestItem> missingItems = gson.fromJson(response.errorBody().string(), responseType);

				return new GenesysResponse().setMissingItems(missingItems);
			}
		}
		catch (IOException e)
		{
			Logger.getLogger("").info(e.getMessage());
			e.printStackTrace();
		}

		return null;
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

	public static boolean isAvailable()
	{
		return service != null;
	}
}
