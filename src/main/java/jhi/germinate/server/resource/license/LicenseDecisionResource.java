package jhi.germinate.server.resource.license;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import static jhi.germinate.server.database.codegen.tables.Licenselogs.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseDecisionResource extends ServerResource
{
	private Integer licenseId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.licenseId = Integer.parseInt(getRequestAttributes().get("licenseId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public void getJson()
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (licenseId == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
		{
			try (DSLContext context = Database.getContext())
			{
				context.insertInto(LICENSELOGS)
					   .set(LICENSELOGS.LICENSE_ID, licenseId)
					   .set(LICENSELOGS.USER_ID, userDetails.getId())
					   .execute();
			}
		}
		else
		{
			CustomVerifier.updateAcceptedDatasets(getRequest(), getResponse(), licenseId);
		}
	}
}
