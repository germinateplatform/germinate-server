package jhi.germinate.server.resource.importers;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.*;

import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.importer.McpdImporter;

/**
 * @author Sebastian Raubach
 */
public class McpdImporterResource extends BaseServerResource
{
	public static final String PARAM_IS_UPDATE = "update";

	private boolean isUpdate = false;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		String isUpdateString = getQueryValue(PARAM_IS_UPDATE);

		if (!StringUtils.isEmpty(isUpdateString))
			isUpdate = Boolean.parseBoolean(isUpdateString);
	}

	@Post
	@MinUserType(UserType.AUTH_USER)
	public String accept(Representation entity)
	{
		try
		{
			File file = createTempFile("mcpd", "xlsx");
			FileUploadHandler.handle(entity, "fileToUpload", file);
			return new McpdImporter(file, isUpdate).run();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
