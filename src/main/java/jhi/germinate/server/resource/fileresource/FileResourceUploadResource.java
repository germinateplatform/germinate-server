package jhi.germinate.server.resource.fileresource;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Path("fileresource")
@MultipartConfig
public class FileResourceUploadResource extends ContextResource
{
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public String postFileResource(@FormDataParam("file") InputStream fileIs, @FormDataParam("file") FormDataContentDisposition fileDetails)
		throws IOException
	{
		// Generate a UUID to identify the file
		String uuid = UUID.randomUUID().toString();

		// Write the representation to a file in the temp directory initially. We'll move it later when the database object is received.

		File folder = new File(System.getProperty("java.io.tmpdir"));
		String itemName = fileDetails.getFileName();
		String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
		File target = new File(folder, uuid + "." + extension);

		if (!FileUtils.isSubDirectory(folder, target))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		Files.copy(fileIs, target.toPath(), StandardCopyOption.REPLACE_EXISTING);

		return target.getName();
	}
}
