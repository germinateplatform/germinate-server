package jhi.germinate.server.resource.gatekeeper;

import jhi.gatekeeper.client.GatekeeperService;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.NewUserAccessRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import retrofit2.Response;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Path("gatekeeper/user/existing")
public class GatekeeperExistingUserResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public jakarta.ws.rs.core.Response postGatekeeperExistingUser(NewUserAccessRequest request)
		throws IOException
	{
		GatekeeperService service = GatekeeperClient.get();

		// Check if the current system exists
		Response<PaginatedResult<List<DatabaseSystems>>> systems = service.getDatabaseSystems(Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();

		if (systems.isSuccessful())
		{
			PaginatedResult<List<DatabaseSystems>> list = systems.body();

			// Check if the user exists
			Users u = new Users();
			u.setUsername(request.getUsername());
			u.setPassword(request.getPassword());
			Response<Token> user = service.postToken(u).execute();

			if (user.isSuccessful())
			{
				Token token = user.body();

				// User logged in successfully and the current system exists
				if (token != null && list != null && !CollectionUtils.isEmpty(list.getData()))
				{
					// Create a new request
					NewAccessRequest newRequest = new NewAccessRequest();
					newRequest.setUserId(token.getId());
					newRequest.setDatabaseSystemId(list.getData().get(0).getId());
					newRequest.setLocale(request.getLocale());
					newRequest.setNeedsApproval((byte) (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL) ? 1 : 0));

					Response<Boolean> response = service.addExistingRequest(newRequest).execute();

					if (response.isSuccessful())
					{
						return jakarta.ws.rs.core.Response.ok(response.body()).build();
					}
					else
					{
						GatekeeperApiError error = GatekeeperClient.parseError(response);
						return jakarta.ws.rs.core.Response.status(response.code())
						                                  .entity(error.getDescription())
						                                  .type(MediaType.TEXT_PLAIN)
						                                  .build();
					}
				}
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(user);
				return jakarta.ws.rs.core.Response.status(user.code())
				                                  .entity(error.getDescription())
				                                  .type(MediaType.TEXT_PLAIN)
				                                  .build();
			}
		}
		else
		{
			GatekeeperApiError error = GatekeeperClient.parseError(systems);
			return jakarta.ws.rs.core.Response.status(systems.code())
			                                  .entity(error.getDescription())
			                                  .type(MediaType.TEXT_PLAIN)
			                                  .build();
		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			resp.sendError(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
//			return false;
//		}

		return jakarta.ws.rs.core.Response.ok(false).build();
	}
}
