package jhi.germinate.server.resource.importers;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class CurlyWhirlyGroupCreationResource extends BaseServerResource
{
	@Post
	@MinUserType(UserType.AUTH_USER)
	public String accept(Representation entity)
	{
		try (DSLContext context = Database.getContext())
		{
			// Create a temp file
			File tempFile = createTempFile("group-upload", "txt");
			// Write the representation to it
			File targetFile = FileUploadHandler.handle(entity, "textfile", tempFile);

			List<String> names = Files.readAllLines(targetFile.toPath());

			List<String> ids = new ArrayList<>();

			// If there are names in the file, look up their ids
			if (!CollectionUtils.isEmpty(names))
			{
				ids = context.selectDistinct(GERMINATEBASE.ID.cast(String.class))
							 .from(GERMINATEBASE)
							 .where(GERMINATEBASE.NAME.in(names))
							 .fetchInto(String.class);
			}

			// Write the ids back
			Files.write(targetFile.toPath(), ids, StandardCharsets.UTF_8);

			tempFile.delete();

			// Then return the name of the file
			return targetFile.getName();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
