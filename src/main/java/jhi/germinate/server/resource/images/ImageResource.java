package jhi.germinate.server.resource.images;

import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImages;
import jhi.germinate.server.database.codegen.tables.records.ImagesRecord;
import jhi.germinate.server.util.*;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Images.*;

@Path("image")
public class ImageResource
{
	@Context
	protected HttpServletResponse resp;

	@PATCH
	@Path("/{imageId}")
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

	@DELETE
	@Path("/{imageId}")
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
				if (!small.exists() && type != ImageType.template)
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
		template
	}
}
