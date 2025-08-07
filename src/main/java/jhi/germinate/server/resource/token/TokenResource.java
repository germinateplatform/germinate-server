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

import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.LoginDetails;
import jhi.germinate.resource.enums.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

import jakarta.ws.rs.*;
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
	public Response deleteToken(LoginDetails user)
		throws IOException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
				.build();
		}

		if (user == null)
		{
			return Response.status(Response.Status.NOT_FOUND)
						   .build();
		}

		AuthenticationFilter.UserDetails sessionUser = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (sessionUser == null || !Objects.equals(sessionUser.getToken(), user.getPassword()))
		{
			return Response.status(Response.Status.FORBIDDEN)
						   .build();
		}

		try
		{
			// Try and see if it's a valid UUID
			UUID.fromString(user.getPassword());
			AuthenticationFilter.removeToken(user.getPassword(), req, resp);
			return Response.ok(true).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Response.ok(false).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postToken(LoginDetails request)
		throws IOException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.NONE)
		{
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
				.build();
		}

		boolean canAccess;
		String userType;

		Users user = new Users();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		try
		{
			retrofit2.Response<Token> response = GatekeeperClient.get().postToken(user).execute();
			Token token = response.body();

			if (response.isSuccessful() && token != null)
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
						return Response.status(Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INSUFFICIENT_PERMISSIONS.name())
									   .build();
					}
					else
					{
						canAccess = true;
					}
				}
				else
				{
					return Response.status(Response.Status.BAD_REQUEST)
								   .build();
				}
			}
			else
			{
				return Response.status(Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name())
							   .build();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name())
						   .build();
		}

		String token;
		String imageToken;

		if (canAccess)
		{
			token = UUID.randomUUID().toString();
			imageToken = UUID.randomUUID().toString();
			AuthenticationFilter.UserDetails details = AuthenticationFilter.addToken(this.req, this.resp, token, imageToken, userType, user.getId());

			AuthorizationFilter.ensureUserDatasetsAvailable(DatasetTableResource.getDatasetTypes(), details);
		}
		else
		{
			return Response.status(Response.Status.FORBIDDEN.getStatusCode(), StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name())
						   .build();
		}

		return Response.ok(new jhi.germinate.resource.Token(token, imageToken, user.getId(), user.getUsername(), user.getFullName(), user.getEmailAddress(), userType, AuthenticationFilter.AGE, System.currentTimeMillis()))
			.build();
	}
}
