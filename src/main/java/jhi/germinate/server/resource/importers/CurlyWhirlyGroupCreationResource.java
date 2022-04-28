package jhi.germinate.server.resource.importers;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.Path;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.DSLContext;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

@Path("group/upload")
@Secured({UserType.AUTH_USER})
@MultipartConfig
public class CurlyWhirlyGroupCreationResource extends ContextResource
{
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String accept(@FormDataParam("textfile") InputStream fileIs, @FormDataParam("textfile") FormDataContentDisposition fileDetails)
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			String uuid = UUID.randomUUID().toString();
			File folder = new File(System.getProperty("java.io.tmpdir"));
			String itemName = fileDetails.getFileName();
			String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
			File targetFile = new File(folder, uuid + "." + extension);

			if (!FileUtils.isSubDirectory(folder, targetFile))
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return null;
			}

			Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

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

			// Then return the name of the file
			return targetFile.getName();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}
}
