package jhi.germinate.server.gatekeeper;

import java.io.*;

import jhi.gatekeeper.client.*;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import okhttp3.*;
import retrofit2.Response;
import retrofit2.*;
import retrofit2.converter.gson.*;

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

	public static void init(String url, String username, String password)
	{
		if (!url.endsWith("/"))
			url += "/";

		GatekeeperClient.url = url;
		GatekeeperClient.username = username;
		GatekeeperClient.password = password;

		reset();
	}

	private static void reset()
	{
		OkHttpClient httpClient = new OkHttpClient.Builder().build();
		Retrofit retrofit = (new Retrofit.Builder()).baseUrl(url)
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		httpClient = (new OkHttpClient.Builder()).addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
																							 .addHeader("Authorization", "Bearer " + token.getToken())
																							 .addHeader("Cookie", "token=" + token.getToken())
																							 .build()))
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
}
