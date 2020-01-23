package jhi.germinate.server.resource.importers;

import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.*;

import jhi.germinate.resource.ImportResult;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.importer.AbstractImporter;

/**
 * @author Sebastian Raubach
 */
public class ImportStatusResource extends ServerResource
{
	private String uuid;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		uuid = getRequestAttributes().get("uuid").toString();
	}

	@Get("json")
	@MinUserType(UserType.AUTH_USER)
	public List<ImportResult> getJson()
	{
		if (uuid != null)
		{
			return AbstractImporter.getStatus(uuid);
		}
		else
		{
			return new ArrayList<>();
		}
	}
}
