package jhi.germinate.server.resource.importers;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

@Path("group/upload")
@Secured({UserType.AUTH_USER})
public class CurlyWhirlyGroupCreationResource extends ContextResource
{
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String accept()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Create a temp file
			File tempFile = ResourceUtils.createTempFile("group-upload", "txt");
			// Write the representation to it
			File targetFile = FileUploadHandler.handle(req, "textfile", tempFile);

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
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}
}
