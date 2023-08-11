package jhi.germinate.server.resource.story;

import com.google.gson.Gson;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableStories;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.IMAGETYPES;
import static jhi.germinate.server.database.codegen.tables.Publications.PUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Stories.STORIES;
import static jhi.germinate.server.database.codegen.tables.Storysteps.STORYSTEPS;

@Path("story")
@Secured(UserType.DATA_CURATOR)
public class StoryIndividualResource extends ContextResource
{
	@DELETE
	@Path("/{storyId:\\d+}/step/{storyStepId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStoryStep(@PathParam("storyId") Integer storyId, @PathParam("storyStepId") Integer storyStepId)
			throws SQLException
	{
		if (storyId == null || storyStepId == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			StoriesRecord story = context.selectFrom(STORIES).where(STORIES.ID.eq(storyId)).fetchAny();

			if (story == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			Result<StorystepsRecord> steps = context.selectFrom(STORYSTEPS).where(STORYSTEPS.STORY_ID.eq(storyId)).orderBy(STORYSTEPS.STORY_INDEX).fetch();

			Optional<StorystepsRecord> stepToDelete = steps.stream().filter(s -> Objects.equals(s.getId(), storyStepId)).findFirst();

			if (stepToDelete.isEmpty())
				return Response.status(Response.Status.NOT_FOUND).build();

			int index = stepToDelete.get().getStoryIndex();

			for (StorystepsRecord record : steps)
			{
				if (Objects.equals(record.getId(), storyStepId))
				{
					record.delete();
				}
				else if (record.getStoryIndex() > index)
				{
					record.setStoryIndex(record.getStoryIndex() - 1);
					record.store(STORYSTEPS.STORY_INDEX);
				}
			}

			return Response.ok().build();
		}
	}

	@POST
	@Path("/{storyId:\\d+}/step")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postStoryStep(@PathParam("storyId") Integer storyId,
								  @FormDataParam("stepName") String stepName,
								  @FormDataParam("stepDescription") String stepDescription,
								  @FormDataParam("image") InputStream image,
								  @FormDataParam("image") FormDataContentDisposition fileDetails,
								  @FormDataParam("pageConfig") String pageConfig,
								  @FormDataParam("storyIndex") Integer storyIndex)
			throws SQLException, IOException
	{
		if (StringUtils.isEmpty(pageConfig))
			return Response.status(Response.Status.BAD_REQUEST).build();

		StoryStepConfig config;
		try
		{
			config = new Gson().fromJson(pageConfig, StoryStepConfig.class);
		}
		catch (Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (storyId == null || pageConfig == null || config.getRouter() == null || StringUtils.isEmpty(config.getRouter().getName()) || StringUtils.isEmpty(stepName) || StringUtils.isEmpty(stepDescription))
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (storyIndex == null)
			storyIndex = 0;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			StoriesRecord story = context.selectFrom(STORIES).where(STORIES.ID.eq(storyId)).fetchAny();

			if (story == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			Result<StorystepsRecord> steps = context.selectFrom(STORYSTEPS).where(STORYSTEPS.STORY_ID.eq(storyId)).orderBy(STORYSTEPS.STORY_INDEX).fetch();

			for (StorystepsRecord step : steps)
			{
				if (step.getStoryIndex() >= storyIndex)
				{
					step.setStoryIndex(step.getStoryIndex() + 1);
					step.store(STORYSTEPS.STORY_INDEX);
				}
			}

			if (story.getRequirements() != null)
			{
				story.setRequirements(new StoryRequirements()
											  .setDatasetIds(new HashSet<>())
											  .setGroupIds(new HashSet<>()));
			}

			if (config.getRouter() != null)
			{
				RouterConfig r = config.getRouter();
				if (r.getParams() != null)
				{
					if (r.getParams().containsKey("datasetId"))
					{
						try
						{
							story.getRequirements().getDatasetIds().add(Integer.parseInt(r.getParams().get("datasetId")));
						}
						catch (Exception e)
						{
							// Do nothing here
						}
					}
					if (r.getParams().containsKey("datasetIds"))
					{
						try
						{
							String[] parts = r.getParams().get("datasetIds").split(",");
							for (String p : parts)
								story.getRequirements().getDatasetIds().add(Integer.parseInt(p));
						}
						catch (Exception e)
						{
							// Do nothing here
						}
					}
					if (r.getParams().containsKey("groupId"))
					{
						try
						{
							story.getRequirements().getGroupIds().add(Integer.parseInt(r.getParams().get("groupId")));
						}
						catch (Exception e)
						{
							// Do nothing here
						}
					}
				}

				story.store(STORIES.REQUIREMENTS);
			}

			StorystepsRecord newStep = context.newRecord(STORYSTEPS);
			newStep.setName(stepName);
			newStep.setDescription(stepDescription);
			newStep.setStoryId(storyId);
			newStep.setStoryIndex(storyIndex);
			newStep.setPageConfig(config);
			boolean successful = newStep.store() > 0;

			if (image != null)
			{
				ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
													.where(IMAGETYPES.REFERENCE_TABLE.eq("storysteps"))
													.fetchAny();

				File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.storysteps.name()), "upload");
				folder.mkdirs();

				String itemName = fileDetails.getFileName();
				String uuid = UUID.randomUUID().toString();
				String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
				File targetFile = new File(folder, uuid + "." + extension);

				if (!FileUtils.isSubDirectory(folder, targetFile))
					return Response.status(Response.Status.BAD_REQUEST).build();

				Files.copy(image, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ImagesRecord imagesRecord = context.newRecord(IMAGES);
				imagesRecord.setForeignId(story.getId());
				imagesRecord.setImagetypeId(imageType.getId());
				imagesRecord.setDescription(targetFile.getName());
				imagesRecord.setPath("upload/" + targetFile.getName());
				imagesRecord.store();

				newStep.setImageId(imagesRecord.getId());
				newStep.store(STORYSTEPS.IMAGE_ID);
			}

			if (successful)
				return Response.status(Response.Status.OK).build();
			else
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postStory(@FormDataParam("storyName") String storyName,
							  @FormDataParam("storyDescription") String storyDescription,
							  @FormDataParam("image") InputStream image,
							  @FormDataParam("image") FormDataContentDisposition fileDetails,
							  @FormDataParam("storyCreatedOn") String storyCreatedOn,
							  @FormDataParam("publicationId") Integer publicationId,
							  @FormDataParam("storyVisibility") Boolean storyVisibility,
							  @FormDataParam("storyFeatured") Boolean storyFeatured)
			throws SQLException, IOException
	{
		if (StringUtils.isEmpty(storyName) || StringUtils.isEmpty(storyDescription))
			return Response.status(Response.Status.BAD_REQUEST).build();

		Date d;

		try
		{
			d = GsonUtil.parseDate(storyCreatedOn);
		}
		catch (Exception e)
		{
			d = new Date();
		}

		if (storyVisibility == null)
			storyVisibility = false;
		if (storyFeatured == null)
			storyFeatured = false;

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			if (publicationId != null)
			{
				boolean publicationExists = context.fetchExists(PUBLICATIONS, PUBLICATIONS.ID.eq(publicationId));

				if (!publicationExists)
					return Response.status(Response.Status.NOT_FOUND).build();
			}

			StoriesRecord story = context.newRecord(STORIES);
			story.setName(storyName);
			story.setDescription(storyDescription);
			story.setCreatedOn(new Timestamp(d.getTime()));
			story.setVisibility(storyVisibility);
			story.setFeatured(storyFeatured);
			story.setPublicationId(publicationId);
			story.setUserId(userDetails.getId());
			boolean successful = story.store() > 0;

			if (image != null)
			{
				ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
													.where(IMAGETYPES.REFERENCE_TABLE.eq("storysteps"))
													.fetchAny();

				File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.storysteps.name()), "upload");
				folder.mkdirs();

				String itemName = fileDetails.getFileName();
				String uuid = UUID.randomUUID().toString();
				String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
				File targetFile = new File(folder, uuid + "." + extension);

				if (!FileUtils.isSubDirectory(folder, targetFile))
					return Response.status(Response.Status.BAD_REQUEST).build();

				Files.copy(image, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ImagesRecord imagesRecord = context.newRecord(IMAGES);
				imagesRecord.setForeignId(story.getId());
				imagesRecord.setImagetypeId(imageType.getId());
				imagesRecord.setDescription(targetFile.getName());
				imagesRecord.setPath("upload/" + targetFile.getName());
				imagesRecord.store();

				story.setImageId(imagesRecord.getId());
				story.store(STORIES.IMAGE_ID);
			}

			if (successful)
				return Response.status(Response.Status.OK).build();
			else
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PATCH
	@Path("/{storyId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response patchStory(@PathParam("storyId") Integer storyId, ViewTableStories update)
			throws SQLException, IOException
	{
		if (storyId == null || update == null || StringUtils.isEmpty(update.getStoryName()) || StringUtils.isEmpty(update.getStoryDescription()))
			return Response.status(Response.Status.BAD_REQUEST).build();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			if (update.getPublicationId() != null)
			{
				boolean publicationExists = context.fetchExists(PUBLICATIONS, PUBLICATIONS.ID.eq(update.getPublicationId()));

				if (!publicationExists)
					return Response.status(Response.Status.NOT_FOUND).build();
			}

			StoriesRecord story = context.selectFrom(STORIES)
										 .where(STORIES.ID.eq(storyId))
										 .and(STORIES.VISIBILITY.eq(true)
																.or(STORIES.USER_ID.ge(userDetails.getId())))
										 .fetchAny();

			if (story == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			story.setName(update.getStoryName());
			story.setDescription(update.getStoryDescription());
			story.setVisibility(update.getStoryVisibility());
			story.setFeatured(update.getStoryFeatured());
			story.setPublicationId(update.getPublicationId());
			story.setCreatedOn(update.getStoryCreatedOn());

			return Response.ok(story.store() > 0).build();
		}
	}

	@DELETE
	@Path("/{storyId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStoryById(@PathParam("storyId") Integer storyId)
			throws SQLException
	{
		if (storyId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			StoriesRecord match = context.selectFrom(STORIES).where(STORIES.ID.eq(storyId)).fetchAny();

			if (match == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			match.delete();

			ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
												.where(IMAGETYPES.REFERENCE_TABLE.eq("storysteps"))
												.fetchAny();

			// Select any image of the `storysteps` type that does NOT have a story referencing it and NOT have a story step referencing it
			Result<ImagesRecord> looseImages = context.selectFrom(IMAGES).where(IMAGES.IMAGETYPE_ID.eq(imageType.getId()))
													  .andNotExists(DSL.selectOne().from(STORIES).where(STORIES.IMAGE_ID.eq(IMAGES.ID)))
													  .andNotExists(DSL.selectOne().from(STORYSTEPS).where(STORYSTEPS.IMAGE_ID.eq(IMAGES.ID)))
													  .fetch();

			if (looseImages.size() > 0)
			{
				File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.storysteps.name());

				for (ImagesRecord image : looseImages)
				{
					File imageFile = new File(folder, image.getPath());

					if (!FileUtils.isSubDirectory(folder, imageFile))
						continue;

					// Delete the image file as well as the image record in the database
					imageFile.delete();
					image.delete();
				}
			}

			return Response.ok().build();
		}
	}
}
