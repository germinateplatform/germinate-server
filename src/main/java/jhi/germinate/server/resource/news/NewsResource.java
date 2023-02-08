package jhi.germinate.server.resource.news;

import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.News;
import jhi.germinate.server.database.codegen.tables.records.NewsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.News.*;

@Path("news")
@Secured({UserType.DATA_CURATOR})
public class NewsResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postNews(News newsItem)
		throws IOException, SQLException
	{
		if (newsItem == null || newsItem.getId() != null || StringUtils.isEmpty(newsItem.getTitle()) || StringUtils.isEmpty(newsItem.getContent()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Check if there's a base64 image to save
			String base64 = newsItem.getImage();
			if (!StringUtils.isEmpty(base64))
			{
				String[] strings = base64.split(",");

				if (strings.length != 2)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return false;
				}

				// Get the extension
				String extension;
				switch (strings[0])
				{
					case "data:image/jpeg;base64":
						extension = "jpeg";
						break;
					case "data:image/png;base64":
						extension = "png";
						break;
					case "data:image/jpg:base64":
						extension = "jpg";
						break;
					default:
						resp.sendError(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
						return false;
				}
				//convert base64 string to binary data
				byte[] bytes = Base64.getDecoder().decode(strings[1].getBytes(StandardCharsets.UTF_8));

				File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.news.name());
				folder.mkdirs();
				java.nio.file.Path image = new File(folder, UUID.randomUUID() + "." + extension).toPath();
				Files.write(image, bytes);

				newsItem.setImage(image.toFile().getName());
			}

			newsItem.setUserId(userDetails.getId() != -1000 ? userDetails.getId() : null);
			if (newsItem.getCreatedOn() == null)
				newsItem.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			if (newsItem.getUpdatedOn() == null)
				newsItem.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			NewsRecord record = context.newRecord(NEWS, newsItem);
			return record.store() > 0;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return false;
		}
	}


	@DELETE
	@Path("/{newsId:\\d+}")
	public boolean deleteNews(@PathParam("newsId") Integer newsId)
		throws IOException, SQLException
	{
		if (newsId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			NewsRecord news = context.selectFrom(NEWS)
									 .where(NEWS.ID.eq(newsId))
									 .fetchAny();

			if (news != null)
			{
				String image = news.getImage();

				if (!StringUtils.isEmpty(image))
				{
					File file = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.news.name()), image);
					File thumb = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.news.name()), "thumbnail-" + image);

					if (file.exists() && file.isFile())
						file.delete();
					if (thumb.exists() && thumb.isFile())
						thumb.delete();
				}

				return news.delete() > 0;
			}

			return false;
		}
	}
}
