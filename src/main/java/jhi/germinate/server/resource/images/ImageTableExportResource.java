package jhi.germinate.server.resource.images;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImages;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableImages.*;

@jakarta.ws.rs.Path("image/table/export")
@Secured
@PermitAll
public class ImageTableExportResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postImageTableExport(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		String name = "images";

		try
		{
			File zipFile = ResourceUtils.createTempFile(null, name, ".zip", false);

			String prefix = zipFile.getAbsolutePath().replace("\\", "/");
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);

			URI uri = URI.create("jar:file:/" + prefix);

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			env.put("encoding", "UTF-8");

			try (Connection conn = Database.getConnection();
				 FileSystem fs = FileSystems.newFileSystem(uri, env, null))
			{
				DSLContext context = Database.getContext(conn);
				SelectJoinStep<Record> from = context.select().from(VIEW_TABLE_IMAGES);

				// Filter here!
				filter(from, filters);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

				setPaginationAndOrderBy(from)
					.fetchInto(ViewTableImages.class)
					.forEach(i -> {
						File source = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), "database"), i.getImagePath());

						if (source.exists())
						{
							String targetPrefix = i.getImageRefTable();
							String targetName;
							if (i.getCreatedOn() != null)
								targetName = sdf.format(new Date(i.getCreatedOn().getTime())) + "-" + i.getReferenceName();
							else
								targetName = i.getReferenceName();
							String fileExtension = i.getImagePath().substring(i.getImagePath().lastIndexOf("."));
							java.nio.file.Path target = fs.getPath("/", targetPrefix, targetName + fileExtension);
							try
							{
								Files.createDirectories(target.getParent());
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}

							int counter = 1;
							while (Files.exists(target))
							{
								String tempName = targetName + "-" + (counter++) + fileExtension;
								target = fs.getPath("/", targetPrefix, tempName);
							}

							try
							{
								Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					});
			}


			Path zipFilePath = zipFile.toPath();
			return Response.ok((StreamingOutput) output -> {
				Files.copy(zipFilePath, output);
				Files.deleteIfExists(zipFilePath);
			})
						   .type("application/zip")
						   .header("content-disposition", "attachment;filename= \"" + zipFile.getName() + "\"")
						   .header("content-length", zipFile.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}
}
