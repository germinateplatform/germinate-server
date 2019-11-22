package jhi.germinate.server.resource.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.Institutions;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.gatekeeper.GatekeeperApiError;
import retrofit2.Response;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperInstitutionResource extends PaginatedServerResource
{
	@Get("json")
	public PaginatedResult<List<Institutions>> getJson()
	{
		try
		{
			Response<PaginatedResult<List<Institutions>>> response = GatekeeperClient.get().getInstitutions(currentPage, pageSize).execute();

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
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
		}
	}
}
