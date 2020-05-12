package jhi.germinate.server.resource.images;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableImages;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.ViewTableImages.*;

/**
 * @author Sebastian Raubach
 */
public class ImageTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		String name = "images";

		FileRepresentation representation;
		try
		{
			File zipFile = createTempFile(null, name, ".zip", false);

			String prefix = zipFile.getAbsolutePath().replace("\\", "/");
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);

			URI uri = URI.create("jar:file:/" + prefix);

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			env.put("encoding", "UTF-8");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 FileSystem fs = FileSystems.newFileSystem(uri, env, null))
			{
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
							Path target = fs.getPath("/", targetPrefix, targetName + fileExtension);
							try
							{
								Files.createDirectories(target.getParent());
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}

							int counter = 1;
							while (Files.exists(target)) {
								String tempName = targetName + "-" + (counter++) + fileExtension;
								target = fs.getPath("/", targetPrefix, tempName);
							}

							Logger.getLogger("").log(Level.INFO, target.toString());

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
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
			representation.setSize(zipFile.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
			// Remember to delete this after the call, we don't need it anymore
			representation.setAutoDeleting(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
