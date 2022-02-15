package jhi.germinate.server.resource.fileresource;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.FileresourcetypesRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableFileresourcetypes.*;

@Path("fileresourcetype")
@Secured
@PermitAll
public class FileResourceTypeResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableFileresourcetypes> getFileResourceType()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectFrom(VIEW_TABLE_FILERESOURCETYPES)
						  .fetchInto(ViewTableFileresourcetypes.class);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public Integer postFileResource(Fileresourcetypes type)
		throws IOException, SQLException
	{
		if (type == null || StringUtils.isEmpty(type.getName()) || type.getId() != null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			FileresourcetypesRecord record = context.newRecord(FILERESOURCETYPES, type);
			record.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			record.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			record.store();

			return record.getId();
		}
	}

	@DELETE
	@Path("/{fileResourceTypeId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean deleteFileResourceType(@PathParam("fileResourceTypeId") Integer fileResourceTypeId)
		throws IOException, SQLException
	{
		if (fileResourceTypeId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Delete all files associated with fileresource database objects.
			File target = ResourceUtils.getFromExternal(resp, Integer.toString(fileResourceTypeId), "data", "download");

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
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return false;
		}
	}
}
