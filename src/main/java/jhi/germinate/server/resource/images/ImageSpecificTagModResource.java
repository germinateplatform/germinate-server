package jhi.germinate.server.resource.images;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.ImageTag;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ImageToTags.IMAGE_TO_TAGS;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Imagetags.IMAGETAGS;

@Path("image/{imageId:\\d+}/tag")
@Secured({UserType.DATA_CURATOR})
public class ImageSpecificTagModResource
{
	@Context
	protected HttpServletResponse resp;

	@PathParam("imageId")
	private Integer imageId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void patchImageSpecificTag(List<ImageTag> tags)
			throws SQLException
	{
		if (imageId == null || CollectionUtils.isEmpty(tags))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Make sure the image exists
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
				throw new NotFoundException();

			// Collect those tags that should already exist in the database vs those that are new
			List<ImageTag> existingTags = new ArrayList<>();
			List<ImageTag> newTags = new ArrayList<>();
			tags.forEach(t -> {
				t.setTagName(t.getTagName().strip().toLowerCase());
				if (t.getTagId() > 0)
					existingTags.add(t);
				else
					newTags.add(t);
			});

			// Now get a mapping of those that actually exist in the database
			Map<String, ImagetagsRecord> dbTags = new HashMap<>();
			context.selectFrom(IMAGETAGS)
				   .where(IMAGETAGS.TAG_NAME.in(tags))
				   .forEach(it -> {
					   dbTags.put(it.getTagName().trim().toLowerCase(), it);
				   });

			// Now check if the ones that should exist actually do
			List<ImageTag> dontActuallyExist = existingTags.stream()
														   .filter(t -> dbTags.containsKey(t.getTagName().strip().toLowerCase()))
														   .toList();
			newTags.addAll(dontActuallyExist);
			existingTags.removeAll(dontActuallyExist);

			// Now we're ready to go. Save the new tags
			for (ImageTag newTag : newTags)
			{
				ImagetagsRecord record = context.newRecord(IMAGETAGS);
				record.setTagName(newTag.getTagName().strip().toLowerCase());
				record.store();
				newTag.setTagId(record.getId());

				dbTags.put(record.getTagName(), record);
			}

			// Now, remove all current mappings
			context.deleteFrom(IMAGE_TO_TAGS)
				   .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId))
				   .execute();

			// Then add them all
			for (ImageTag newTag : newTags)
			{
				ImageToTagsRecord record = context.newRecord(IMAGE_TO_TAGS);
				record.setImageId(imageId);
				record.setImagetagId(newTag.getTagId());
				record.store();
			}
			for (ImageTag existingTag : existingTags)
			{
				ImageToTagsRecord record = context.newRecord(IMAGE_TO_TAGS);
				record.setImageId(imageId);
				record.setImagetagId(existingTag.getTagId());
				record.store();
			}

			// Remove any tag that no longer has an image associated with it
			context.deleteFrom(IMAGETAGS)
				   .whereNotExists(DSL.selectOne().from(IMAGE_TO_TAGS).where(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID)))
				   .execute();
		}
	}

//	@PUT
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public void putSpecificImageTag(String[] tags)
//			throws IOException, SQLException
//	{
//		if (imageId == null || tags == null)
//		{
//			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
//			return;
//		}
//
//		try (Connection conn = Database.getConnection())
//		{
//			DSLContext context = Database.getContext(conn);
//			// Make sure the image exists
//			ImagesRecord image = context.selectFrom(IMAGES)
//										.where(IMAGES.ID.eq(imageId))
//										.fetchAny();
//
//			if (image == null)
//			{
//				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
//				return;
//			}
//
//			// Get the new tags, exclude null and trim them all
//			List<String> newTags = Arrays.stream(tags)
//										 .filter(Objects::nonNull)
//										 .map(String::strip)
//										 .map(String::toLowerCase)
//										 .toList();
//
//			// Get the existing tags from the database
//			List<String> existingTags = context.select(DSL.lower(IMAGETAGS.TAG_NAME))
//											   .from(IMAGETAGS)
//											   .leftJoin(IMAGE_TO_TAGS).on(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID))
//											   .fetchInto(String.class);
//
//			// Check which tags are actually new
//			List<String> toAdd = new ArrayList<>(newTags);
//			toAdd.removeAll(existingTags);
//
//			// Delete all old tag mappings
//			context.deleteFrom(IMAGE_TO_TAGS)
//				   .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId))
//				   .execute();
//
//			if (!CollectionUtils.isEmpty(toAdd))
//			{
//				// Insert new tags
//				InsertValuesStep1<ImagetagsRecord, String> step = context.insertInto(IMAGETAGS, IMAGETAGS.TAG_NAME);
//				toAdd.forEach(step::values);
//				step.execute();
//			}
//
//			// Now get the mapping between tag name and tag id for all tags
//			Map<String, Integer> idMapping = context.selectFrom(IMAGETAGS)
//													.fetchMap(IMAGETAGS.TAG_NAME, IMAGETAGS.ID);
//			// Insert new mappings
//			InsertValuesStep2<ImageToTagsRecord, Integer, Integer> step = context.insertInto(IMAGE_TO_TAGS, IMAGE_TO_TAGS.IMAGE_ID, IMAGE_TO_TAGS.IMAGETAG_ID);
//			newTags.forEach(t -> step.values(imageId, idMapping.get(t)));
//			step.execute();
//
//			// Remove any tag that no longer has an image associated with it
//			context.deleteFrom(IMAGETAGS).whereNotExists(DSL.selectOne().from(IMAGE_TO_TAGS).where(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID))).execute();
//		}
//	}
}
