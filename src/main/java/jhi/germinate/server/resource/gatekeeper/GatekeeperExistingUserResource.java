package jhi.germinate.server.resource.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.List;

import jhi.gatekeeper.client.GatekeeperService;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.NewUserAccessRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;
import jhi.germinate.server.util.gatekeeper.GatekeeperApiError;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import retrofit2.Response;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperExistingUserResource extends BaseServerResource
{
	@Post("json")
	public Boolean postJson(NewUserAccessRequest request)
	{
		GatekeeperService service = GatekeeperClient.get();

		try
		{
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
							throw new ResourceException(response.code(), error.getDescription());
						}
					}
				}
				else
				{
					GatekeeperApiError error = GatekeeperClient.parseError(user);
					throw new ResourceException(user.code(), error.getDescription());
				}
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(systems);
				throw new ResourceException(systems.code(), error.getDescription());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
		}

		return false;
	}
}
