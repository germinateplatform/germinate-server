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

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.LoginDetails;
import jhi.germinate.resource.enums.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.StatusMessage;

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
	public boolean deleteToken(LoginDetails user, @Context HttpServletResponse response)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
			throw new ServiceUnavailableException();

		if (user == null)
			throw new NotFoundException();

		AuthenticationFilter.UserDetails sessionUser = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (sessionUser == null || !Objects.equals(sessionUser.getToken(), user.getPassword()))
			throw new ForbiddenException();

		try
		{
			// Try and see if it's a valid UUID
			UUID.fromString(user.getPassword());
			AuthenticationFilter.removeToken(user.getPassword(), req, response);
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
	public jhi.germinate.resource.Token postToken(LoginDetails request, @Context HttpServletResponse response)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
			throw new ServiceUnavailableException();

		boolean canAccess;
		String userType;

		Users user = new Users();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		try
		{
			retrofit2.Response<Token> gkResponse = GatekeeperClient.get().postToken(user).execute();
			Token token = gkResponse.body();

			if (gkResponse.isSuccessful() && token != null)
			{
				user.setId(token.getId());
				retrofit2.Response<PaginatedResult<List<ViewUserPermissions>>> permissions = GatekeeperClient.get().getUserPermissions(token.getId(), Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();

				if (permissions.isSuccessful() && permissions.body() != null)
				{
					userType = permissions.body().getData().stream()
					                      .map(ViewUserPermissions::getUserType)
					                      .filter(p -> !Objects.equals(p, "Suspended User"))
					                      .findFirst()
					                      .orElse(null);

					if (StringUtils.isEmpty(userType))
					{
						throw new ForbiddenException(StatusMessage.FORBIDDEN_INSUFFICIENT_PERMISSIONS.name());
					}
					else
					{
						canAccess = true;
					}
				}
				else
				{
					throw new BadRequestException();
				}
			}
			else
			{
				throw new ForbiddenException(StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ForbiddenException(StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
		}

		String token;
		String imageToken;

		if (canAccess)
		{
			token = UUID.randomUUID().toString();
			imageToken = UUID.randomUUID().toString();
			AuthenticationFilter.UserDetails details = AuthenticationFilter.addToken(req, response, token, imageToken, userType, user.getId());

			AuthorizationFilter.ensureUserDatasetsAvailable(req, details);
		}
		else
		{
			throw new ForbiddenException(StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name());
		}

		return new jhi.germinate.resource.Token(token, imageToken, user.getId(), user.getUsername(), user.getFullName(), user.getEmailAddress(), userType, AuthenticationFilter.AGE, System.currentTimeMillis());
	}
}
