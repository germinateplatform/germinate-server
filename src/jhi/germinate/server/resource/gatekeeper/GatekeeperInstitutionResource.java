package jhi.germinate.server.resource.gatekeeper;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.Institutions;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.PaginatedServerResource;

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
			return GatekeeperClient.get().getInstitutions(currentPage, pageSize).execute().body();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
		}
	}
}
