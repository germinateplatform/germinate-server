/*
 * Copyright 2018 Information & Computational Sciences, The James Hutton Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jhi.germinate.server.auth;

import org.restlet.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.resource.*;
import org.restlet.routing.Route;
import org.restlet.security.Verifier;
import org.restlet.util.Series;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Germinate;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class CustomVerifier implements Verifier
{
	public static final long AGE = 1800000;

	private static Map<String, UserDetails> tokenToTimestamp  = new ConcurrentHashMap<>();
	private static Map<String, String>      tokenToImageToken = new ConcurrentHashMap<>();

	public CustomVerifier()
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				tokenToTimestamp.entrySet().removeIf(token -> {
					boolean expired = token.getValue().timestamp < (System.currentTimeMillis() - AGE);

					if (expired)
						tokenToImageToken.remove(token.getKey());

					return expired;
				});
			}
		}, 0, 60000);
	}

	public static boolean removeToken(String token, Request request, Response response)
	{
		UserDetails exists = tokenToTimestamp.remove(token);

		if (exists != null)
		{
			tokenToImageToken.remove(exists.imageToken);
			setCookie(request, response, null);
			setDatasetCookie(request, response, true);
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Integer getUserId(Request request)
	{
		Series<Header> headers = request.getHeaders();
		String token = null;

		for (Header header : headers)
		{
			if (header.getName().equals("authorization"))
			{
				token = header.getValue();

				if (token != null)
					token = token.replace("Bearer ", "");

				break;
			}
		}

		if (token != null && tokenToTimestamp.containsKey(token))
			return tokenToTimestamp.get(token).id;
		else
			return null;
	}

	private static TokenResult getToken(Request request, Response response)
	{
		ChallengeResponse cr = request.getChallengeResponse();
		if (cr != null)
		{
			TokenResult result = new TokenResult();
			result.token = cr.getRawValue();

			if (StringUtils.isEmpty(result.token) || result.token.equalsIgnoreCase("null"))
				result.token = null;

			// If we do, validate it against the cookie
			List<Cookie> cookies = request.getCookies()
										  .stream()
										  .filter(c -> c.getName().equals("token"))
										  .collect(Collectors.toList());

			if (cookies.size() > 0)
			{
				result.match = Objects.equals(result.token, cookies.get(0).getValue());

				if (!result.match)
					setCookie(request, response, null);
			}
			else
			{
				if (result.token == null)
					return null;
				else
					result.match = true;
			}

			return result;
		}

		return null;
	}

	public static UserDetails getFromSession(Request request, Response response)
	{
		TokenResult token = getToken(request, response);

		// If there is no token or token and cookie don't match, remove the cookie
		if (token == null || !token.match)
			setCookie(request, response, null);

		if (token == null)
		{
			// We get here if no token is found at all
			return new UserDetails(-1000, null, null, UserType.UNKNOWN, AGE);
		}
		else if (!StringUtils.isEmpty(token.token) && !token.match)
		{
			// We get here if a token is provided, but no matching cookie is found.
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}
		else
		{
			// We get here if the token is present and it matches the cookie
			UserDetails details = token.token != null ? tokenToTimestamp.get(token.token) : null;

			if (details == null)
				throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

			return details;
		}
	}

	public static void addToken(Request request, Response response, String token, String imageToken, String userType, Integer userId)
	{
		setCookie(request, response, token);
		UserDetails details = new UserDetails();
		details.timestamp = System.currentTimeMillis();
		details.imageToken = imageToken;
		details.token = token;
		switch (userType)
		{
			case "Administrator":
				details.userType = UserType.ADMIN;
				break;
			case "Data Curator":
				details.userType = UserType.DATA_CURATOR;
				break;
			case "Regular User":
				details.userType = UserType.AUTH_USER;
				break;
			case "Suspended User":
			default:
				details.userType = UserType.UNKNOWN;
		}
		details.id = userId;
		tokenToTimestamp.put(token, details);
		tokenToImageToken.put(token, details.imageToken);
	}

	private static void setCookie(Request request, Response response, String token)
	{
		boolean delete = StringUtils.isEmpty(token);

		CookieSetting cookie = new CookieSetting(0, "token", token);
		cookie.setAccessRestricted(true);
		cookie.setMaxAge(delete ? 0 : (int) (AGE / 1000));
		cookie.setPath("/");
		response.getCookieSettings().add(cookie);

		setDatasetCookie(request, response, false);
	}

	private static void setDatasetCookie(Request request, Response response, boolean delete)
	{
		Set<Integer> ids = getAcceptedDatasets(request);
		if (!CollectionUtils.isEmpty(ids))
		{
			CookieSetting cookie = new CookieSetting(0, "accepted-licenses", CollectionUtils.join(ids, ","));
			cookie.setAccessRestricted(true);
			cookie.setMaxAge(delete ? 0 : (int) (AGE / 1000));
			cookie.setPath("/");
			response.getCookieSettings().add(cookie);
		}
	}

	public static boolean isValidImageToken(String imageToken)
	{
		return tokenToImageToken.containsValue(imageToken);
	}

	public static void updateAcceptedDatasets(Request request, Response response, Integer licenseId)
	{
		Set<Integer> ids = getAcceptedDatasets(request);
		ids.add(licenseId);

		CookieSetting cookie = new CookieSetting(0, "accepted-licenses", CollectionUtils.join(ids, ","));
		cookie.setAccessRestricted(true);
		cookie.setMaxAge((int) (AGE / 1000));
		cookie.setPath("/");
		response.getCookieSettings().add(cookie);
	}

	private boolean canAccess(UserType minUserType, AuthenticationMode mode, UserDetails userDetails)
	{
		switch (mode)
		{
			case FULL:
				// In full authentication mode, just rely on the users type.
				return userDetails != null && userDetails.isAtLeast(minUserType);
			case SELECTIVE:
				// In selective mode, users may or may not be logged in. Check their status for those calls that require a specific user type.
				if (minUserType == UserType.ADMIN || minUserType == UserType.DATA_CURATOR || minUserType == UserType.AUTH_USER)
					return userDetails != null && userDetails.isAtLeast(minUserType);
				else
					return true;
			case NONE:
				// In no-auth mode, allow it for those methods without user type requirements.
				return minUserType != UserType.ADMIN && minUserType != UserType.DATA_CURATOR && minUserType != UserType.AUTH_USER;
			default:
				return false;
		}
	}

	public static Set<Integer> getAcceptedDatasets(Request request)
	{
		return request.getCookies().stream()
					  .filter(c -> c.getName().equals("accepted-licenses"))
					  .map(c -> {
						  String[] values = c.getValue().split(",");
						  Set<Integer> result = new HashSet<>();
						  for (String value : values)
						  {
							  try
							  {
								  result.add(Integer.parseInt(value));
							  }
							  catch (NumberFormatException e)
							  {
							  }
						  }
						  return result;
					  })
					  .findFirst()
					  .orElse(new HashSet<>());
	}

	@Override
	public int verify(Request request, Response response)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		Route route = Germinate.INSTANCE.routerAuth.getRoutes().getBest(request, response, 0);
		String method = request.getMethod().getName();

		if (route != null)
		{

			Finder finder = (Finder) route.getNext();

			Class<?> clazz = finder.getTargetClass();
			Boolean passesAnnotationRequirements = Arrays.stream(clazz.getDeclaredMethods())
														 // Only get the ones that have the annotation
														 .filter(m -> m.isAnnotationPresent(MinUserType.class))
														 // Then filter for all Java methods that have a matching HTTP method annotation
														 .filter(m -> Arrays.stream(m.getDeclaredAnnotations()) // Stream over all annotations
																			.map(a -> a.annotationType().getSimpleName()) // Map them to their simple name
																			.anyMatch(method::equalsIgnoreCase)) // Compare them to the request method
														 .map(m -> {
															 MinUserType annotation = m.getAnnotation(MinUserType.class);
															 UserType userType = annotation.value();
															 return canAccess(userType, mode, getFromSession(request, response));
														 })
														 .findFirst()
														 .orElse(true);

			if (!passesAnnotationRequirements)
				return RESULT_INVALID;
		}

		// If we're in full auth mode OR in selective but it's not a GET or a POST, then check credentials
		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && !Objects.equals(method, "POST") && !Objects.equals(method, "GET")))
		{
			ChallengeResponse cr = request.getChallengeResponse();
			if (cr != null)
			{
				TokenResult token = getToken(request, response);

				if (token != null && token.token != null)
				{
					boolean canAccess = false;

					// Check if it's a valid token
					UserDetails details = tokenToTimestamp.get(token.token);

					if (details != null)
					{
						// First, check the bearer token and see if we have it in the cache
						if ((System.currentTimeMillis() - AGE) < details.timestamp)
						{
							canAccess = true;
							// Extend the cookie
							details.timestamp = System.currentTimeMillis();
							tokenToTimestamp.put(token.token, details);
							setCookie(request, response, token.token);
						}
						else
						{
							return RESULT_STALE;
						}
					}

					if (canAccess)
					{
						// Extend the cookie here
						setCookie(request, response, token.token);
						return RESULT_VALID;
					}
					else
					{
						removeToken(token.token, request, response);
						return RESULT_INVALID;
					}
				}
				else
				{
					removeToken(null, request, response);
					return RESULT_INVALID;
				}
			}
			else
			{
				return RESULT_MISSING;
			}
		}
		else
		{
			return RESULT_VALID;
		}
	}

	private static class TokenResult
	{
		private String  token;
		private boolean match;
	}

	public static class UserDetails
	{
		private Integer  id;
		private String   token;
		private String   imageToken;
		private UserType userType = UserType.UNKNOWN;
		private Long     timestamp;

		public UserDetails()
		{
		}

		public UserDetails(Integer id, String token, String imageToken, UserType userType, Long timestamp)
		{
			this.id = id;
			this.token = token;
			this.imageToken = imageToken;
			this.userType = userType;
			this.timestamp = timestamp;
		}

		public Integer getId()
		{
			return id;
		}

		public String getToken()
		{
			return token;
		}

		public String getImageToken()
		{
			return imageToken;
		}

		public boolean isAtLeast(UserType atLeast)
		{
			switch (atLeast)
			{
				case ADMIN:
					return userType == UserType.ADMIN;
				case DATA_CURATOR:
					return userType == UserType.ADMIN || userType == UserType.DATA_CURATOR;
				case AUTH_USER:
					return userType == UserType.ADMIN || userType == UserType.DATA_CURATOR || userType == UserType.AUTH_USER;
			}

			return false;
		}

		public Long getTimestamp()
		{
			return timestamp;
		}

		@Override
		public String toString()
		{
			return "UserDetails{" +
				"id=" + id +
				", token='" + token + '\'' +
				", imageToken='" + imageToken + '\'' +
				", userType=" + userType +
				", timestamp=" + timestamp +
				'}';
		}
	}
}
