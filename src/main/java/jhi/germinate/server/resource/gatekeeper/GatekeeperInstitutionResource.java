package jhi.germinate.server.resource.gatekeeper;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.Institutions;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.GatekeeperClient;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import retrofit2.Response;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("gatekeeper/institution")
public class GatekeeperInstitutionResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Institutions>> getGatekeeperInstitution()
		throws IOException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
		{
//			try
//			{
			Response<PaginatedResult<List<Institutions>>> response = GatekeeperClient.get().getInstitutions(currentPage, pageSize).execute();

			if (response.isSuccessful())
			{
				return response.body();
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(response);
				resp.sendError(response.code(), error.getDescription());
				return null;
			}
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//				resp.sendError(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
//				return null;
//			}
		}
		else
		{
			resp.sendError(jakarta.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			return null;
		}
	}
}
