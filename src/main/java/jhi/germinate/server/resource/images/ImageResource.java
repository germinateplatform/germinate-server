package jhi.germinate.server.resource.images;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImages;
import jhi.germinate.server.database.codegen.tables.records.ImagesRecord;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.Objects;

import static jhi.germinate.server.database.codegen.tables.Images.*;

/**
 * @author Sebastian Raubach
 */
public class ImageResource extends ServerResource
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

	@Patch
	@MinUserType(UserType.DATA_CURATOR)
	public boolean patchJson(ViewTableImages imageToPatch)
	{
		if (imageId == null || imageToPatch == null || !Objects.equals(imageId, imageToPatch.getImageId()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			image.setDescription(imageToPatch.getImageDescription());
			image.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			return image.store() > 0;
		}
	}

	@Delete
	@MinUserType(UserType.DATA_CURATOR)
	public boolean deleteJson()
	{
		if (imageId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			ImagesRecord image = context.selectFrom(IMAGES)
										.where(IMAGES.ID.eq(imageId))
										.fetchAny();

			if (image == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			File large = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageSourceResource.ImageType.database.name()), image.getPath());
			File small = new File(large.getParentFile(), "thumbnail-" + large.getName());

			if (large.exists() && large.isFile())
				large.delete();
			if (small.exists() && small.isFile())
				small.delete();

			return image.delete() > 0;
		}
	}
}
