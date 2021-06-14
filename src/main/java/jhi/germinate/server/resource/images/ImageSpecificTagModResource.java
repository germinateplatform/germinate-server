package jhi.germinate.server.resource.images;

import jhi.germinate.resource.ImageTagModificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.ImageToTags.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetags.*;

@Path("image/{imageId}/tag")
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
	public void patchImageSpecificTag(ImageTagModificationRequest request)
		throws IOException, SQLException
	{
		if (imageId == null || request == null || request.getTags() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Make sure the image exists
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return;
			}

			List<String> tags = Arrays.stream(request.getTags())
									  .filter(Objects::nonNull)
									  .map(String::strip)
									  .map(String::toLowerCase)
									  .collect(Collectors.toList());

			if (request.isAddition())
			{
				Map<String, ImagetagsRecord> existingTags = context.selectFrom(IMAGETAGS)
																   .where(IMAGETAGS.TAG_NAME.in(tags))
																   .fetchMap(IMAGETAGS.TAG_NAME);

				// Map the tags to new records
				List<ImageToTagsRecord> newRecords = tags.stream()
														 .map(t -> {
															 ImagetagsRecord dbTag = existingTags.get(t);
															 if (dbTag == null)
															 {
																 // If the tag doesn't exist, create it
																 dbTag = context.newRecord(IMAGETAGS);
																 dbTag.setTagName(t);
																 dbTag.setCreatedOn(new Timestamp(System.currentTimeMillis()));
																 dbTag.store();
															 }

															 ImageToTagsRecord newRecord = context.newRecord(IMAGE_TO_TAGS);
															 newRecord.setImageId(imageId);
															 newRecord.setImagetagId(dbTag.getId());
															 return newRecord;
														 })
														 .collect(Collectors.toList());

				// Load them all in this way, because we have to enable `onDuplicateKeyIgnore()`, which isn't possible using the UpdatableRecord#store() method.
				context.loadInto(IMAGE_TO_TAGS)
					   .onDuplicateKeyIgnore()
					   .loadRecords(newRecords)
					   .fields(IMAGE_TO_TAGS.fields())
					   .execute();
			}
			else
			{
				context.deleteFrom(IMAGE_TO_TAGS)
					   .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId))
					   .and(IMAGE_TO_TAGS.IMAGETAG_ID.in(DSL.select(IMAGETAGS.ID)
															.from(IMAGETAGS)
															.where(IMAGETAGS.TAG_NAME.in(tags))))
					   .execute();
			}

			// Remove any tag that no longer has an image associated with it
			context.deleteFrom(IMAGETAGS).whereNotExists(DSL.selectOne().from(IMAGE_TO_TAGS).where(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID))).execute();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return;
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void putSpecificImageTag(String[] tags)
		throws IOException, SQLException
	{
		if (imageId == null || tags == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Make sure the image exists
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return;
			}

			// Get the new tags, exclude null and trim them all
			List<String> newTags = Arrays.stream(tags)
										 .filter(Objects::nonNull)
										 .map(String::strip)
										 .map(String::toLowerCase)
										 .collect(Collectors.toList());

			// Get the existing tags from the database
			List<String> existingTags = context.select(IMAGETAGS.TAG_NAME.lower())
											   .from(IMAGETAGS)
											   .leftJoin(IMAGE_TO_TAGS).on(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID))
											   .fetchInto(String.class);

			// Check which tags are actually new
			List<String> toAdd = new ArrayList<>(newTags);
			toAdd.removeAll(existingTags);

			// Delete all old tag mappings
			context.deleteFrom(IMAGE_TO_TAGS)
				   .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId))
				   .execute();

			if (!CollectionUtils.isEmpty(toAdd))
			{
				// Insert new tags
				InsertValuesStep1<ImagetagsRecord, String> step = context.insertInto(IMAGETAGS, IMAGETAGS.TAG_NAME);
				toAdd.forEach(step::values);
				step.execute();
			}

			// Now get the mapping between tag name and tag id for all tags
			Map<String, Integer> idMapping = context.selectFrom(IMAGETAGS)
													.fetchMap(IMAGETAGS.TAG_NAME, IMAGETAGS.ID);
			// Insert new mappings
			InsertValuesStep2<ImageToTagsRecord, Integer, Integer> step = context.insertInto(IMAGE_TO_TAGS, IMAGE_TO_TAGS.IMAGE_ID, IMAGE_TO_TAGS.IMAGETAG_ID);
			newTags.forEach(t -> step.values(imageId, idMapping.get(t)));
			step.execute();

			// Remove any tag that no longer has an image associated with it
			context.deleteFrom(IMAGETAGS).whereNotExists(DSL.selectOne().from(IMAGE_TO_TAGS).where(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID))).execute();
		}
	}
}
