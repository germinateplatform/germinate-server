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
	public jakarta.ws.rs.core.Response getGatekeeperInstitution()
		throws IOException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
		{
//			try
//			{
			Response<PaginatedResult<List<Institutions>>> response = GatekeeperClient.get().getInstitutions(currentPage, pageSize).execute();

			if (response.isSuccessful())
			{
				return jakarta.ws.rs.core.Response.ok(response.body()).build();
			}
			else
			{
				GatekeeperApiError error = GatekeeperClient.parseError(response);
				return jakarta.ws.rs.core.Response.status(response.code(), error.getDescription()).build();
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
			return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE.getStatusCode()).build();
		}
	}
}
