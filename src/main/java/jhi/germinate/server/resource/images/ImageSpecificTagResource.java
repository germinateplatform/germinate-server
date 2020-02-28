package jhi.germinate.server.resource.images;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.ImageTagModificationRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.Imagetags;
import jhi.germinate.server.database.tables.records.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ImageToTags.*;
import static jhi.germinate.server.database.tables.Images.*;
import static jhi.germinate.server.database.tables.Imagetags.*;

/**
 * @author Sebastian Raubach
 */
public class ImageSpecificTagResource extends PaginatedServerResource
{
	private Integer imageId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			imageId = Integer.parseInt(getRequestAttributes().get("imageId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}
	}

	@Patch("json")
	@MinUserType(UserType.DATA_CURATOR)
	public void patchJson(ImageTagModificationRequest request)
	{
		if (imageId == null || request == null || request.getTags() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// Make sure the image exists
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			List<String> tags = Arrays.stream(request.getTags())
									  .filter(Objects::nonNull)
									  .map(String::trim)
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
		}
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

	}

	@Put("json")
	@MinUserType(UserType.DATA_CURATOR)
	public void putJson(String[] tags)
	{
		if (imageId == null || CollectionUtils.isEmpty(tags))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// Make sure the image exists
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			// Get the new tags, exclude null and trim them all
			List<String> newTags = Arrays.stream(tags)
										 .filter(Objects::nonNull)
										 .map(String::trim)
										 .collect(Collectors.toList());

			// Get the existing tags from the database
			List<String> existingTags = context.selectFrom(IMAGETAGS.leftJoin(IMAGE_TO_TAGS).on(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID)))
											   .fetch(IMAGETAGS.TAG_NAME);

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
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Get("json")
	public PaginatedResult<List<Imagetags>> getJson()
	{
		if (imageId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing image id");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(IMAGE_TO_TAGS)
													 .leftJoin(IMAGETAGS).on(IMAGETAGS.ID.eq(IMAGE_TO_TAGS.IMAGE_ID))
													 .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId));

			List<Imagetags> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Imagetags.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
