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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("gatekeeper/user/existing")
public class GatekeeperExistingUserResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean postGatekeeperExistingUser(NewUserAccessRequest request)
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
						return response.body();
					}
					else
					{
						GatekeeperApiError error = GatekeeperClient.parseError(response);
						resp.sendError(response.code(), error.getDescription());
						return false;
					}
				}
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(user);
				resp.sendError(user.code(), error.getDescription());
				return false;
			}
		}
		else
		{
			GatekeeperApiError error = GatekeeperClient.parseError(systems);
			resp.sendError(systems.code(), error.getDescription());
			return false;
		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			resp.sendError(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
//			return false;
//		}

		return false;
	}
}
