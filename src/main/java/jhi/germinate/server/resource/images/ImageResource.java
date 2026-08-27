package jhi.germinate.server.resource.images;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImages;
import jhi.germinate.server.database.codegen.tables.records.ImagesRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import lombok.*;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.ViewTableImages.VIEW_TABLE_IMAGES;

@Path("image")
public class ImageResource extends ContextResource
{
	@Context
	protected HttpServletResponse resp;

	@PATCH
	@Path("/{imageId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean patchImage(ViewTableImages imageToPatch, @PathParam("imageId") Integer imageId)
			throws SQLException
	{
		if (imageId == null || imageToPatch == null || !Objects.equals(imageId, imageToPatch.getImageId()))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ImagesRecord image = context.selectFrom(IMAGES)
			                            .where(IMAGES.ID.eq(imageId))
			                            .fetchAny();

			if (image == null)
				throw new NotFoundException();

			image.setDescription(imageToPatch.getImageDescription());
			image.setIsReference(imageToPatch.getImageIsReference());
			image.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			return image.store() > 0;
		}
	}

	@POST
	@Path("/carousel")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public String postTemplateImage(@FormDataParam("locale") String locale, @FormDataParam("imageFile") InputStream fileIs, @FormDataParam("imageFile") FormDataContentDisposition fileDetails)
			throws IOException
	{
		if (StringUtils.isEmpty(locale))
			throw new BadRequestException();

		File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageType.template.name());
		folder.mkdirs();

		String itemName = fileDetails.getFileName();
		String uuid = UUID.randomUUID().toString();
		String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
		File targetFile = new File(folder, locale + "-" + uuid + "." + extension);

		if (!FileUtils.isSubDirectory(folder, targetFile))
			throw new BadRequestException();

		Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		return targetFile.getName();
	}

	@DELETE
	@Path("/{name:[a-zA-Z0-9\\-\\_]+\\.[a-zA-Z]{3}}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean deleteImageByName(@PathParam("name") String name)
	{
		if (StringUtils.isEmpty(name))
			throw new BadRequestException();

		// Get the template images folder
		File parent = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images");
		// Get the file within it
		File large = new File(new File(parent, ImageType.template.name()), name);

		// Check it's actually a child of the template images folder
		if (!FileUtils.isSubDirectory(parent, large))
			throw new ForbiddenException();
		// Then check it exists
		if (!large.exists())
			throw new NotFoundException();

		// Get the thumbnail
		File small = new File(large.getParentFile(), "thumbnail-" + large.getName());

		// Delete original and optionally the thumbnail
		large.delete();
		if (small.exists())
			small.delete();

		return true;
	}

	@DELETE
	@Path("/{imageId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean deleteImage(@PathParam("imageId") Integer imageId)
			throws SQLException
	{
		if (imageId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ImagesRecord image = context.selectFrom(IMAGES)
			                            .where(IMAGES.ID.eq(imageId))
			                            .fetchAny();

			if (image == null)
				throw new NotFoundException();

			File large = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageType.database.name()), image.getPath());
			File small = new File(large.getParentFile(), "thumbnail-" + large.getName());

			if (large.exists() && large.isFile())
				large.delete();
			if (small.exists() && small.isFile())
				small.delete();

			return image.delete() > 0;
		}
	}

	@GET
	@Path("/src/{name}")
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImageNameDummy(@QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
	{
		FileResult result = getImageLocal(imageType, name, size, token, true);

		return toImageResponse(result.data, result.mime);
	}

	@GET
	@Path("/{imageId:\\d+}/src")
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImageByID(@PathParam("imageId") Integer imageId, @QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableImages image = context.selectFrom(VIEW_TABLE_IMAGES)
			                               .where(VIEW_TABLE_IMAGES.IMAGE_ID.eq(imageId))
			                               .fetchAnyInto(ViewTableImages.class);

			if (image == null)
				throw new NotFoundException();
			else
			{
				// Trait reference images are free to view so GridScore can get to them without a token
				boolean checkToken = !image.getImageRefTable().equals("phenotypes") || !image.getImageIsReference();
				FileResult result = getImageLocal(imageType, image.getImagePath(), size, token, checkToken);

				return toImageResponse(result.data, result.mime);
			}
		}
	}

	@GET
	@Path("/src")
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImage(@QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
	{
		FileResult result = getImageLocal(imageType, name, size, token, true);

		return toImageResponse(result.data, result.mime);
	}

	private FileResult getImageLocal(String imageType, String name, String size, String token, boolean checkToken)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		ImageType type;
		try
		{
			type = ImageType.valueOf(imageType);
		}
		catch (Exception e)
		{
			type = null;
		}

		if (type == null)
			throw new BadRequestException();

		// If it's not a template image, check the image token
		if (mode == AuthenticationMode.FULL && type != ImageType.template)
		{
			if (checkToken && (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token)))
				throw new ForbiddenException();
		}

		if (!StringUtils.isEmpty(name))
		{
			File parent = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images");
			File large = new File(new File(parent, type.name()), name);

			if (!FileUtils.isSubDirectory(parent, large))
				throw new ForbiddenException();

			File small = new File(large.getParentFile(), "thumbnail-" + large.getName());

			name = large.getName();
			String extension = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

			String mediaType;

			switch (extension)
			{
				case "jpg":
				case "jpeg":
					mediaType = "image/jpeg";
					break;
				case "png":
					mediaType = "image/png";
					break;
				case "svg":
					mediaType = "image/svg+xml";
					break;
				default:
					mediaType = "image/*";
			}

			if (large.exists() && large.isFile())
			{
				if (!small.exists() && !Objects.equals(extension, "svg"))
				{
					try
					{
						Thumbnails.of(large)
						          .height(500)
						          .keepAspectRatio(true)
						          .toFile(small);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				File file;

				if (StringUtils.isEmpty(size) || size.equals("large"))
					file = large;
				else
					file = small;

				return new FileResult()
						.setData(file)
						.setMime(mediaType);
			}
			else
			{
				throw new NotFoundException();
			}
		}
		else
		{
			throw new BadRequestException();
		}
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@ToString
	@Accessors(chain = true)
	private static class FileResult
	{
		File   data;
		String mime;
	}

	public enum ImageType
	{
		climate,
		database,
		news,
		template,
		mapoverlay,
		storysteps,
		projects
	}
}
