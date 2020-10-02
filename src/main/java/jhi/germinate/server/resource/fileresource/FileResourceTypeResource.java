package jhi.germinate.server.resource.fileresource;

import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.*;
import java.sql.*;
import java.util.List;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.FileresourcetypesRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableFileresourcetypes.*;

/**
 * @author Sebastian Raubach
 */
public class FileResourceTypeResource extends ServerResource
{
	private Integer fileResourceTypeId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.fileResourceTypeId = Integer.parseInt(getRequestAttributes().get("fileResourceTypeId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Delete
	@MinUserType(UserType.DATA_CURATOR)
	public boolean deleteJson()
	{
		if (fileResourceTypeId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			// Delete all files associated with fileresource database objects.
			File target = BaseServerResource.getFromExternal(Integer.toString(fileResourceTypeId), "data", "download");

			try
			{
				FileUtils.deleteDirectory(target);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}

			// Delete the fileresource type. This will trigger the deletion of the referencing fileresouces.
			return context.deleteFrom(FILERESOURCETYPES)
						  .where(FILERESOURCETYPES.ID.eq(fileResourceTypeId))
						  .execute() > 0;
		}
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public Integer postJson(Fileresourcetypes type)
	{
		if (type == null || StringUtils.isEmpty(type.getName()) || type.getId() != null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			FileresourcetypesRecord record = context.newRecord(FILERESOURCETYPES, type);
			record.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			record.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			record.store();

			return record.getId();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Get
	public List<ViewTableFileresourcetypes> getJson()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.selectFrom(VIEW_TABLE_FILERESOURCETYPES)
						  .fetchInto(ViewTableFileresourcetypes.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
