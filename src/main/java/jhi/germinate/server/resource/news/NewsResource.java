package jhi.germinate.server.resource.news;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.News;
import jhi.germinate.server.database.tables.records.NewsRecord;
import jhi.germinate.server.resource.images.ImageSourceResource;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.News.*;

/**
 * @author Sebastian Raubach
 */
public class NewsResource extends ServerResource
{
	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public boolean postJson(News newsItem)
	{
		if (newsItem == null || newsItem.getId() != null || StringUtils.isEmpty(newsItem.getTitle()) || StringUtils.isEmpty(newsItem.getContent()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// Check if there's a base64 image to save
			String base64 = newsItem.getImage();
			if (!StringUtils.isEmpty(base64))
			{
				String[] strings = base64.split(",");

				if (strings.length != 2)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

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
						throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
				}
				//convert base64 string to binary data
				byte[] bytes = Base64.getDecoder().decode(strings[1].getBytes(StandardCharsets.UTF_8));

				Path image = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageSourceResource.ImageType.news.name()), UUID.randomUUID() + "." + extension).toPath();
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
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
