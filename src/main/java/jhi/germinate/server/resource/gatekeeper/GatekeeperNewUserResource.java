package jhi.germinate.server.resource.gatekeeper;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.*;
import jhi.gatekeeper.server.database.tables.pojos.DatabaseSystems;
import jhi.germinate.resource.NewUnapprovedUserRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Path("gatekeeper/user/new")
public class GatekeeperNewUserResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Boolean postJson(NewUnapprovedUserRequest request)
			throws IOException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
		{
//			try
//			{
			Response<PaginatedResult<List<DatabaseSystems>>> systems = GatekeeperClient.get().getDatabaseSystems(Database.getDatabaseServer(), Database.getDatabaseName(), 0, Integer.MAX_VALUE).execute();

			if (systems.isSuccessful())
			{
				PaginatedResult<List<DatabaseSystems>> list = systems.body();

				if (list != null && !CollectionUtils.isEmpty(list.getData()))
				{
					NewUnapprovedUser user = request.getUser();
					if (StringUtils.isEmpty(user.getUserUsername()))
						user.setUserUsername(user.getUserEmailAddress());
					user.setNeedsApproval((byte) (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL) ? 1 : 0));
					user.setDatabaseSystemId(list.getData().get(0).getId());
					user.setLocale(request.getLocale());

					Response<Boolean> response = GatekeeperClient.get().addNewRequest(user).execute();

					if (response.isSuccessful())
					{
						return response.body();
					}
					else
					{
						GatekeeperApiError error = GatekeeperClient.parseError(response);

						throw new StatusException(response.code(), error.getDescription());
					}
				}
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(systems);
				throw new StatusException(systems.code(), error.getDescription());
			}

			return false;
		}
		else
		{
			throw new ServiceUnavailableException();
		}
	}
}
