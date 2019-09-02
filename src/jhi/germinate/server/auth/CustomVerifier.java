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
import org.restlet.data.*;
import org.restlet.resource.Finder;
import org.restlet.routing.Route;
import org.restlet.security.Verifier;
import org.restlet.util.Series;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Germinate;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class CustomVerifier implements Verifier
{
	public static final long AGE = 1800000;

	private static Map<String, UserDetails> tokenToTimestamp = new ConcurrentHashMap<>();

	public CustomVerifier()
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				tokenToTimestamp.entrySet().removeIf(token -> token.getValue().timestamp < (System.currentTimeMillis() - AGE));
			}
		}, 0, 60000);
	}

	public static boolean removeToken(Request request)
	{
		return tokenToTimestamp.remove(getToken(request)) != null;
	}

	public static boolean removeToken(String token)
	{
		return tokenToTimestamp.remove(token) != null;
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

	private static String getToken(Request request)
	{
		ChallengeResponse cr = request.getChallengeResponse();
		if (cr != null)
		{
			String token = cr.getRawValue();

			// If we do, validate it against the cookie
			List<Cookie> cookies = request.getCookies()
										  .stream()
										  .filter(c -> c.getName().equals("token"))
										  .collect(Collectors.toList());

			if (cookies.size() > 0)
				return Objects.equals(token, cookies.get(0).getValue()) ? token : null;
			else
				return null;
		}

		return null;
	}

	public static UserDetails getFromSession(Request request)
	{
		String token = getToken(request);
		return token == null ? new UserDetails(-1000, null, UserType.UNKNOWN, AGE) : tokenToTimestamp.get(token);
	}

	public static void addToken(Response response, String token, String userType, Integer userId)
	{
		setCookie(response, token);
		UserDetails details = new UserDetails();
		details.timestamp = System.currentTimeMillis();
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
	}

	private static void setCookie(Response response, String token)
	{
		CookieSetting cookie = new CookieSetting(0, "token", token);
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

	@Override
	public int verify(Request request, Response response)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		Route route = Germinate.INSTANCE.routerAuth.getRoutes().getBest(request, response, 0);

		Finder finder = (Finder) route.getNext();

		Class<?> clazz = finder.getTargetClass();
		String method = request.getMethod().getName();
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
														 return canAccess(userType, mode, getFromSession(request));
													 })
													 .findFirst()
													 .orElse(true);

		if (!passesAnnotationRequirements)
			return RESULT_INVALID;

		// If we're in full auth mode OR in selective but it's not a GET or a POST, then check credentials
		// TODO: Is this safe? Aren't there POSTs that need logged in users or are they covered by the previous check?
		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && !Objects.equals(method, "POST") && !Objects.equals(method, "GET")))
		{
			ChallengeResponse cr = request.getChallengeResponse();
			if (cr != null)
			{
				String token = getToken(request);

				if (token != null)
				{
					boolean canAccess = false;

					// Check if it's a valid token
					UserDetails details = tokenToTimestamp.get(token);

					if (details != null)
					{
						// First, check the bearer token and see if we have it in the cache
						if ((System.currentTimeMillis() - AGE) < details.timestamp)
						{
							canAccess = true;
							// Extend the cookie
							details.timestamp = System.currentTimeMillis();
							tokenToTimestamp.put(token, details);
							setCookie(response, token);
						}
						else
						{
							return RESULT_STALE;
						}
					}

					return canAccess ? RESULT_VALID : RESULT_INVALID;
				}
				else
				{
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

	public static class UserDetails
	{
		private Integer  id;
		private String   token;
		private UserType userType = UserType.UNKNOWN;
		private Long     timestamp;

		public UserDetails()
		{
		}

		public UserDetails(Integer id, String token, UserType userType, Long timestamp)
		{
			this.id = id;
			this.token = token;
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

			return true;
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
				", timestamp=" + timestamp +
				'}';
		}
	}
}
