package jhi.germinate.server.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.*;

import jhi.gatekeeper.client.GatekeeperService;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.AuthenticationMode;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.gatekeeper.GatekeeperApiError;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import okhttp3.*;
import retrofit2.Response;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

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

	private static ConcurrentHashMap<Integer, ViewUserDetails> users = new ConcurrentHashMap<>();
	private static Retrofit                                    retrofit;

	public static void init(String url, String username, String password)
	{
		if (StringUtils.isEmpty(url) || StringUtils.isEmpty(username))
			return;

		if (!url.endsWith("/"))
			url += "/";

		if (!url.endsWith("api/"))
			url += "api/";

		GatekeeperClient.url = url;
		GatekeeperClient.username = username;
		GatekeeperClient.password = password;

		try
		{
			reset();
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	private static void reset()
	{
		OkHttpClient httpClient = new OkHttpClient.Builder()
			.readTimeout(1, TimeUnit.MINUTES)
			.callTimeout(1, TimeUnit.MINUTES)
			.connectTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.retryOnConnectionFailure(true)
			.build();
		retrofit = (new Retrofit.Builder()).baseUrl(url)
										   .addConverterFactory(GsonConverterFactory.create())
										   .client(httpClient)
										   .build();

		service = retrofit.create(GatekeeperService.class);

		Users user = new Users();
		user.setUsername(username);
		user.setPassword(password);
		try
		{
			Response<Token> response = service.postToken(user).execute();

			if (response.isSuccessful())
				GatekeeperClient.token = response.body();
			else
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		httpClient = (new OkHttpClient.Builder())
			.readTimeout(1, TimeUnit.MINUTES)
			.callTimeout(1, TimeUnit.MINUTES)
			.connectTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.retryOnConnectionFailure(true)
			.addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
														.addHeader("Authorization", "Bearer " + token.getToken())
														.addHeader("Cookie", "token=" + token.getToken())
														.build()))
			.addInterceptor(chain -> {
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
			return new GatekeeperApiError();
		}

		return error;
	}

	public static ViewUserDetails getUser(Integer id)
	{
		if (id == null)
		{
			return null;
		}
		else if (users == null)
		{
			if (PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class) != AuthenticationMode.NONE)
			{
				ViewUserDetails result = users.get(id);

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
}
