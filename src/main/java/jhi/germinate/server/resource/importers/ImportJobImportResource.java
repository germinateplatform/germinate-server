package jhi.germinate.server.resource.importers;

import org.restlet.resource.*;

import java.util.*;

import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.DataImportJobs;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.StringUtils;

/**
 * @author Sebastian Raubach
 */
public class ImportJobImportResource extends BaseServerResource implements AsyncResource
{
	private String jobUuid;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.jobUuid = getRequestAttributes().get("jobUuid").toString();

			try
			{
				// Check if it's a valid UUID
				UUID.fromString(this.jobUuid);
			}
			catch (IllegalArgumentException e)
			{
				this.jobUuid = null;
			}
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	@MinUserType(UserType.DATA_CURATOR)
	public List<AsyncExportResult> getJson()
	{
		if (StringUtils.isEmpty(jobUuid))
			return new ArrayList<>();

		return DataImportRunner.importData(jobUuid);
	}
}
