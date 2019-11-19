package jhi.germinate.server.resource.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.List;

import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.DatabaseSystems;
import jhi.germinate.resource.NewUnapprovedUserRequest;
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
public class GatekeeperNewUserResource extends BaseServerResource
{
	@Post("json")
	public Boolean postJson(NewUnapprovedUserRequest request)
	{
		try
		{
			Response<PaginatedResult<List<DatabaseSystems>>> systems = GatekeeperClient.get().getDatabaseSystems(Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();

			if (systems.isSuccessful())
			{
				PaginatedResult<List<DatabaseSystems>> list = systems.body();

				if (list != null && !CollectionUtils.isEmpty(list.getData()))
				{
					NewUnapprovedUser user = request.getUser();
					user.setNeedsApproval((byte) (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL) ? 1 : 0));
					user.setDatabaseSystemId(list.getData().get(0).getId());
					user.setLocale(request.getLocale());
					return GatekeeperClient.get().addNewRequest(user).execute().body();
				}
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, systems.errorBody().string());
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
