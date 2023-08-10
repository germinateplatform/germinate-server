package jhi.germinate.server.resource.images;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.CarouselConfig;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.ImagesRecord;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.DSLContext;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;

@Path("image")
public class ImageResource
{
	@Context
	protected HttpServletResponse resp;

	@PATCH
	@Path("/{imageId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean patchImage(ViewTableImages imageToPatch, @PathParam("imageId") Integer imageId)
			throws IOException, SQLException
	{
		if (imageId == null || imageToPatch == null || !Objects.equals(imageId, imageToPatch.getImageId()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			image.setDescription(imageToPatch.getImageDescription());
			image.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			return image.store() > 0;
		}
	}

	@POST
	@Path("/template")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean postTemplateImage(@FormDataParam("locales") List<String> locales, @FormDataParam("imageFile") InputStream fileIs, @FormDataParam("imageFile") FormDataContentDisposition fileDetails)
			throws IOException
	{
		if (CollectionUtils.isEmpty(locales))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageType.template.name());
		folder.mkdirs();

		String itemName = fileDetails.getFileName();
		String uuid = UUID.randomUUID().toString();
		String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
		File targetFile = new File(folder, uuid + "." + extension);

		if (!FileUtils.isSubDirectory(folder, targetFile))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		// Read the carousel.json file
		File configFile = ResourceUtils.getFromExternal(resp, "carousel.json", "template");

		CarouselConfig config;
		Gson gson = new Gson();
		Type type = new TypeToken<CarouselConfig>()
		{
		}.getType();
		if (configFile.exists())
		{
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				config = gson.fromJson(br, type);
			}
		}
		else
		{
			config = new CarouselConfig();

			locales.forEach(l -> config.put(l, new ArrayList<>()));
		}

		locales.forEach(l -> config.get(l).add(new CarouselConfig.ImageConfig().setName(targetFile.getName()).setText("")));

		// Write the file back
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
		{
			gson.toJson(config, type, writer);
		}

		return true;
	}

	@DELETE
	@Path("/{name:[a-zA-Z0-9\\-]+\\.[a-zA-Z]{3}}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean deleteImageByName(@PathParam("name") String name)
			throws IOException
	{
		if (StringUtils.isEmpty(name))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		// Get the template images folder
		File parent = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images");
		// Get the file within it
		File large = new File(new File(parent, ImageType.template.name()), name);

		// Check it's actually a child of the template images folder
		if (!FileUtils.isSubDirectory(parent, large))
		{
			resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
			return false;
		}
		// Then check it exists
		if (!large.exists())
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return false;
		}

		// Get the thumbnail
		File small = new File(large.getParentFile(), "thumbnail-" + large.getName());
		// Read the carousel.json file
		File configFile = ResourceUtils.getFromExternal(resp, "carousel.json", "template");
		CarouselConfig config;
		Gson gson = new Gson();
		Type type = new TypeToken<CarouselConfig>()
		{
		}.getType();

		try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
		{
			config = gson.fromJson(br, type);

			for (Map.Entry<String, List<CarouselConfig.ImageConfig>> entry : config.entrySet())
			{
				if (!CollectionUtils.isEmpty(entry.getValue()))
				{
					// Remove those entries that match based on the filename
					entry.setValue(entry.getValue().stream().filter(i -> !Objects.equals(i.getName(), large.getName())).collect(Collectors.toList()));
				}
			}
		}

		// Write the file back
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
		{
			gson.toJson(config, type, writer);
		}

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
			throws IOException, SQLException
	{
		if (imageId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImageNameDummy(@QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
			throws IOException
	{
		return this.getImage(imageType, name, size, token);
	}

	@GET
	@Path("/{imageId:\\d+}/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImageByID(@PathParam("imageId") Integer imageId, @QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
			throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Images image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAnyInto(Images.class);

			if (image == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			else
				return getImage(imageType, image.getPath(), size, token);
		}
	}

	@GET
	@Path("/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/svg+xml", "image/*"})
	public Response getImage(@QueryParam("type") String imageType, @QueryParam("name") String name, @QueryParam("size") String size, @QueryParam("token") String token)
			throws IOException
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
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		// If it's not a template image, check the image token
		if (mode == AuthenticationMode.FULL && type != ImageType.template)
		{
			if (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}
		}

		if (!StringUtils.isEmpty(name))
		{
			File parent = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images");
			File large = new File(new File(parent, type.name()), name);

			if (!FileUtils.isSubDirectory(parent, large))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}

			File small = new File(large.getParentFile(), "thumbnail-" + large.getName());

			name = large.getName();
			String extension = name.substring(name.lastIndexOf(".") + 1, name.length() - 1).toLowerCase();

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

				try
				{
					byte[] bytes = IOUtils.toByteArray(file.toURI());

					return Response.ok(new ByteArrayInputStream(bytes))
								   .header("Content-Type", mediaType)
								   .build();
				}
				catch (IOException e)
				{
					e.printStackTrace();

					resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
					return null;
				}
			}
			else
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}
		}
		else
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}
	}

	public enum ImageType
	{
		climate,
		database,
		news,
		template,
		mapoverlay,
		storysteps
	}
}
