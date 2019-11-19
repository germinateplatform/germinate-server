package jhi.germinate.server.resource.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.List;

import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.*;
import jhi.germinate.resource.NewUserAccessRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;
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
		try
		{
			Response<PaginatedResult<List<DatabaseSystems>>> systems = GatekeeperClient.get().getDatabaseSystems(Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();
			Users u = new Users();
			u.setUsername(request.getUsername());
			u.setPassword(request.getPassword());
			Response<Token> user = GatekeeperClient.get().postToken(u).execute();

			if (systems.isSuccessful() && user.isSuccessful())
			{
				PaginatedResult<List<DatabaseSystems>> list = systems.body();
				Token token = user.body();

				if (token != null && list != null && !CollectionUtils.isEmpty(list.getData()))
				{
					NewAccessRequest newRequest = new NewAccessRequest();
					newRequest.setUserId(token.getId());
					newRequest.setDatabaseSystemId(list.getData().get(0).getId());
					newRequest.setLocale(request.getLocale());
					newRequest.setNeedsApproval((byte) (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL) ? 1 : 0));
					return GatekeeperClient.get().addExistingRequest(newRequest).execute().body();
				}
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, systems.errorBody().string() + " " + user.errorBody().string());
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
