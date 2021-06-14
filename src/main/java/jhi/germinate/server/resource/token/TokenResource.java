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

package jhi.germinate.server.resource.token;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import retrofit2.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
@Path("token")
public class TokenResource extends ContextResource
{
	public static Integer SALT = 10;

	@DELETE
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteToken(LoginDetails user)
		throws IOException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			resp.sendError(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			return false;
		}

		if (user == null)
		{
			resp.sendError(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode(), StatusMessage.NOT_FOUND_TOKEN.name());
			return false;
		}

		AuthenticationFilter.UserDetails sessionUser = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (sessionUser == null || !Objects.equals(sessionUser.getToken(), user.getPassword()))
		{
			resp.sendError(javax.ws.rs.core.Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_ACCESS_TO_OTHER_USER.name());
			return false;
		}

		try
		{
			// Try and see if it's a valid UUID
			UUID.fromString(user.getPassword());
			AuthenticationFilter.removeToken(user.getPassword(), req, resp);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Token postToken(LoginDetails request)
		throws IOException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			resp.sendError(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			return null;
		}

		boolean canAccess = false;
		String userType = UserType.UNKNOWN.name();

		Users user = new Users();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		try
		{
			Response<jhi.gatekeeper.resource.Token> response = GatekeeperClient.get().postToken(user).execute();
			jhi.gatekeeper.resource.Token token = response.body();

			if (response.isSuccessful() && token != null)
			{
				user.setId(token.getId());
				Response<PaginatedResult<List<ViewUserPermissions>>> permissions = GatekeeperClient.get().getUserPermissions(token.getId(), Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();

				if (permissions.isSuccessful() && permissions.body() != null)
				{
					userType = permissions.body().getData().stream()
										  .map(ViewUserPermissions::getUserType)
										  .filter(p -> !Objects.equals(p, "Suspended User"))
										  .findFirst()
										  .orElse(null);

					if (StringUtils.isEmpty(userType))
						this.resp.sendError(javax.ws.rs.core.Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INSUFFICIENT_PERMISSIONS.name());
					else
						canAccess = true;
				}
				else
				{
					this.resp.sendError(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}
			}
			else
			{
				this.resp.sendError(javax.ws.rs.core.Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
				return null;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.resp.sendError(javax.ws.rs.core.Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
			return null;
		}

		String token;
		String imageToken;

		if (canAccess)
		{
			token = UUID.randomUUID().toString();
			imageToken = UUID.randomUUID().toString();
			AuthenticationFilter.addToken(this.req, this.resp, token, imageToken, userType, user.getId());
		}
		else
		{
			this.resp.sendError(javax.ws.rs.core.Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
			return null;
		}

		return new Token(token, imageToken, user.getId(), user.getUsername(), user.getFullName(), user.getEmailAddress(), userType, AuthenticationFilter.AGE, System.currentTimeMillis());
	}
}
