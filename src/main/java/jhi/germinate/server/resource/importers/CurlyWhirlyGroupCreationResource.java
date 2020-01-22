package jhi.germinate.server.resource.importers;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.*;

import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.BaseServerResource;

/**
 * @author Sebastian Raubach
 */
public class CurlyWhirlyGroupCreationResource extends BaseServerResource
{
	@Post
	@MinUserType(UserType.AUTH_USER)
	public String accept(Representation entity)
	{
		try
		{
			// Create a temp file
			File file = createTempFile("group-upload", "txt");
			// Write the representation to it
			FileUploadHandler.handle(entity, "textfile", file);
			// Then return the name of the file
			return file.getName();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
