package jhi.germinate.server.resource.importers;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class CurlyWhirlyGroupCreationResource extends BaseServerResource
{
	@Post
	@MinUserType(UserType.AUTH_USER)
	public String accept(Representation entity)
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// Create a temp file
			File file = createTempFile("group-upload", "txt");
			// Write the representation to it
			FileUploadHandler.handle(entity, "textfile", file);

			List<String> names = Files.readAllLines(file.toPath());

			List<String> ids = new ArrayList<>();

			// If there are names in the file, look up their ids
			if (!CollectionUtils.isEmpty(names))
				ids = context.selectDistinct(GERMINATEBASE.ID.cast(String.class))
							 .from(GERMINATEBASE)
							 .where(GERMINATEBASE.NAME.in(names))
							 .fetchInto(String.class);

			// Write the ids back
			Files.write(file.toPath(), ids, StandardCharsets.UTF_8);

			// Then return the name of the file
			return file.getName();
		}
		catch (IOException | SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
