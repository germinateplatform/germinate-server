package jhi.germinate.server.resource.images;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.records.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.importers.FileUploadHandler;
import jhi.germinate.server.util.ExifUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.Compounds.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Images.*;
import static jhi.germinate.server.database.tables.Imagetypes.*;
import static jhi.germinate.server.database.tables.Phenotypes.*;

/**
 * @author Sebastian Raubach
 */
public class ImageUploadResource extends BaseServerResource
{
	private String  referenceTable;
	private Integer foreignId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.foreignId = Integer.parseInt(getRequestAttributes().get("foreignId").toString());
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		try
		{
			this.referenceTable = getRequestAttributes().get("referenceTable").toString();
		}
		catch (NullPointerException e)
		{
		}
	}

	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public boolean postImage(Representation entity)
	{
		if (foreignId == null || referenceTable == null || entity == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
												.where(IMAGETYPES.REFERENCE_TABLE.eq(referenceTable))
												.fetchAny();

			Record record = null;
			switch (imageType.getReferenceTable())
			{
				case "germinatebase":
					record = context.selectFrom(GERMINATEBASE)
									.where(GERMINATEBASE.ID.eq(foreignId))
									.fetchAny();
					break;
				case "phenotypes":
					record = context.selectFrom(PHENOTYPES)
									.where(PHENOTYPES.ID.eq(foreignId))
									.fetchAny();
					break;
				case "compounds":
					record = context.selectFrom(COMPOUNDS)
									.where(COMPOUNDS.ID.eq(foreignId))
									.fetchAny();
					break;
			}

			if (record == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

			File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageSourceResource.ImageType.database.name()), "upload");
			folder.mkdirs();

			List<String> finalFilenames = FileUploadHandler.handleMultiple(entity, "imageFiles", folder);

			int counter = 0;

			for (String finalFilename : finalFilenames)
			{
				File imageFile = new File(folder, finalFilename);

				Date date = ExifUtils.getCreatedOnOrClosest(imageFile);

				ImagesRecord image = context.newRecord(IMAGES);
				image.setForeignId(foreignId);
				image.setImagetypeId(imageType.getId());
				image.setPath("upload/" + finalFilename);
				image.setDescription(finalFilename);
				if (date != null)
					image.setCreatedOn(new Timestamp(date.getTime()));
				counter += image.store();
			}

			return counter > 0;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
