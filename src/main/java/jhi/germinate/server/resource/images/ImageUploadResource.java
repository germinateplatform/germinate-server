package jhi.germinate.server.resource.images;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Compounds.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;

@Path("image/upload/{referenceTable}/{foreignId}")
@Secured({UserType.DATA_CURATOR})
@MultipartConfig
public class ImageUploadResource
{
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postImage(@PathParam("referenceTable") String referenceTable, @PathParam("foreignId") Integer foreignId, @FormDataParam("imageFiles") InputStream fileIs, @FormDataParam("imageFiles") FormDataContentDisposition fileDetails)
		throws IOException, SQLException
	{
		if (foreignId == null || referenceTable == null || req == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
												.where(IMAGETYPES.REFERENCE_TABLE.eq(referenceTable))
												.fetchAny();

			Record record = null;
			switch (imageType.getReferenceTable())
			{
				case "germinatebase":
					record = context.selectFrom(GERMINATEBASE)
									.where(GERMINATEBASE.ID.eq(foreignId))
									.fetchAny();
					break;
				case "phenotypes":
					record = context.selectFrom(PHENOTYPES)
									.where(PHENOTYPES.ID.eq(foreignId))
									.fetchAny();
					break;
				case "compounds":
					record = context.selectFrom(COMPOUNDS)
									.where(COMPOUNDS.ID.eq(foreignId))
									.fetchAny();
					break;
			}

			if (record == null)
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.database.name()), "upload");
			folder.mkdirs();

			String itemName = fileDetails.getFileName();
			String uuid = UUID.randomUUID().toString();
			String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
			File targetFile = new File(folder, uuid + "." + extension);

			if (!FileUtils.isSubDirectory(folder, targetFile)) {
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			Date date = ExifUtils.getCreatedOnOrClosest(targetFile);

			ImagesRecord image = context.newRecord(IMAGES);
			image.setForeignId(foreignId);
			image.setImagetypeId(imageType.getId());
			image.setPath("upload/" + targetFile.getName());
			image.setDescription(targetFile.getName());
			if (date != null)
				image.setCreatedOn(new Timestamp(date.getTime()));
			image.store();

			return true;
		}
	}
}
