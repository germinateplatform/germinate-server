package jhi.germinate.server.resource.fileresource;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.ViewTableFileresources;
import jhi.germinate.server.database.tables.records.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.importers.FileUploadHandler;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.Fileresources.*;
import static jhi.germinate.server.database.tables.Fileresourcetypes.*;

/**
 * @author Sebastian Raubach
 */
public class FileResourceResource extends BaseServerResource
{
	private Integer fileResourceId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.fileResourceId = Integer.parseInt(getRequestAttributes().get("fileResourceId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Delete
	@MinUserType(UserType.DATA_CURATOR)
	public boolean deleteJson()
	{
		if (fileResourceId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			FileresourcesRecord fileResource = context.selectFrom(FILERESOURCES)
													  .where(FILERESOURCES.ID.eq(fileResourceId))
													  .fetchAny();

			if (fileResource != null)
			{
				String path = fileResource.getPath();

				if (!StringUtils.isEmpty(path))
				{
					File file = BaseServerResource.getFromExternal(path, "data", "download");

					if (file.exists() && file.isFile())
						file.delete();
				}

				return fileResource.delete() > 0;
			}

			return false;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Get
	public FileRepresentation getJson()
	{
		if (fileResourceId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			FileresourcesRecord record = context.selectFrom(FILERESOURCES)
												.where(FILERESOURCES.ID.eq(fileResourceId))
												.fetchAny();

			if (record == null)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

			File resultFile = BaseServerResource.getFromExternal(record.getPath(), "data", "download");

			if (!resultFile.exists() || !resultFile.isFile())
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);


			MediaType type = MediaType.ALL;
			String filename = resultFile.getName();

			try
			{
				String mimeType = Files.probeContentType(resultFile.toPath());
				type = MediaType.valueOf(mimeType);

				filename = record.getName().replaceAll("[^a-zA-Z0-9-_.]", "-") + filename.substring(filename.lastIndexOf("."));
			}
			catch (IOException | IndexOutOfBoundsException e)
			{
			}

			FileRepresentation representation = new FileRepresentation(resultFile, type);
			representation.setSize(resultFile.length());

			Logger.getLogger("").info("FILENAME: " + filename);

			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename(filename);
			disp.setSize(resultFile.length());
			representation.setDisposition(disp);
			return representation;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Put
	@MinUserType(UserType.DATA_CURATOR)
	public boolean putJson(ViewTableFileresources fileResource)
	{
		if (fileResource == null || fileResource.getFileresourceId() != null || fileResource.getFileresourcetypeId() == null || StringUtils.isEmpty(fileResource.getFileresourcePath()) || StringUtils.isEmpty(fileResource.getFileresourceName()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			FileresourcetypesRecord type = context.selectFrom(FILERESOURCETYPES)
												  .where(FILERESOURCETYPES.ID.eq(fileResource.getFileresourcetypeId()))
												  .fetchAny();

			// Get the file reference from the tmp directory
			File source = new File(new File(System.getProperty("java.io.tmpdir")), fileResource.getFileresourcePath());

			// If the type doesn't exist or the source file isn't available fail
			if (type == null || !source.exists() || !source.isFile())
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

			// Get the target location for this file
			File target = BaseServerResource.getFromExternal(fileResource.getFileresourcePath(), "data", "download");
			target.getParentFile().mkdirs();

			try
			{
				// Move the file from the temp directory to the actual Germinate directory
				Files.move(source.toPath(), target.toPath());
			}
			catch (IOException e)
			{
				// If the operation fails, delete the source.
				source.delete();
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			// If we get here the file was successfully found and moved
			FileresourcesRecord record = context.newRecord(FILERESOURCES);
			record.setName(fileResource.getFileresourceName());
			record.setPath(fileResource.getFileresourcePath());
			record.setFilesize(target.length());
			record.setDescription(fileResource.getFileresourceDescription());
			record.setFileresourcetypeId(type.getId());
			record.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			record.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			return record.store() > 0;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public String accept(Representation entity)
	{
		// Generate a UUID to identify the file
		String uuid = UUID.randomUUID().toString();

		// Write the representation to a file in the temp directory initially. We'll move it later when the database object is received.
		return FileUploadHandler.handle(entity, "file", new File(System.getProperty("java.io.tmpdir"), uuid));
	}
}
