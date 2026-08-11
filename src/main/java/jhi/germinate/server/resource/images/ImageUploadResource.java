package jhi.germinate.server.resource.images;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.*;
import org.jooq.Record;

import java.io.*;
import java.io.File;
import java.nio.file.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.IMAGETYPES;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;

@Path("image/upload")
@MultipartConfig
public class ImageUploadResource
{
	@POST
	@Path("/template")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.ADMIN})
	public String postTemplateImage(@FormDataParam("image") InputStream fileIs, @FormDataParam("image") FormDataContentDisposition fileDetails)
			throws IOException
	{
		File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.template.name());
		folder.mkdirs();

		String itemName = fileDetails.getFileName();
		String uuid = UUID.randomUUID().toString();
		String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
		File targetFile = new File(folder, uuid + "." + extension);

		if (!FileUtils.isSubDirectory(folder, targetFile))
			throw new BadRequestException();

		Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		return targetFile.getName();
	}

	@POST
	@Path("/{referenceTable}/{foreignId:\\d+}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean postImage(@PathParam("referenceTable") String referenceTable, @PathParam("foreignId") Integer foreignId, @FormDataParam("imageFiles") InputStream fileIs, @FormDataParam("imageFiles") FormDataContentDisposition fileDetails)
			throws IOException, SQLException
	{
		if (foreignId == null || referenceTable == null)
			throw new BadRequestException();

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
				case "variables":
					record = context.selectFrom(VARIABLES)
					                .where(VARIABLES.ID.eq(foreignId))
					                .fetchAny();
					break;
			}

			if (record == null)
				throw new BadRequestException();

			File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.database.name()), "upload");
			folder.mkdirs();

			String itemName = fileDetails.getFileName();
			String uuid = UUID.randomUUID().toString();
			String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
			File targetFile = new File(folder, uuid + "." + extension);

			if (!FileUtils.isSubDirectory(folder, targetFile))
				throw new BadRequestException();

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
