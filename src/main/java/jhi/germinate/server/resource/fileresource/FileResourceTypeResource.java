package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.FileresourcetypesRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.apache.commons.io.FileUtils;
import org.jooq.*;

import java.io.File;
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
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectWhereStep<?> step = context.selectFrom(VIEW_TABLE_FILERESOURCETYPES);

			if (!userDetails.isAtLeast(UserType.DATA_CURATOR))
				step.where(VIEW_TABLE_FILERESOURCETYPES.PUBLIC_VISIBILITY.eq(true));

			return step.fetchInto(ViewTableFileresourcetypes.class);
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
	@Path("/{fileResourceTypeId:\\d+}")
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

			FileresourcetypesRecord record = context.selectFrom(FILERESOURCETYPES)
													.where(FILERESOURCETYPES.ID.eq(fileResourceTypeId))
													// Don't allow deletion of these new default resource types
													.andNot(FILERESOURCETYPES.NAME.eq("Trials Shapefile").and(FILERESOURCETYPES.DESCRIPTION.eq("Shape file associated with a phenotypic trial. Fields within the shape file have to match the database entries.")))
													.andNot(FILERESOURCETYPES.NAME.eq("Trials GeoTIFF").and(FILERESOURCETYPES.DESCRIPTION.eq("GeoTIFF file associated with a phenotypic trial. The \"created_on\" date of this fileresource determines the time point at which it was recorded.")))
													.fetchAny();

			if (record != null)
			{
				// Delete all files associated with fileresource database objects.
				File target = ResourceUtils.getFromExternal(resp, Integer.toString(record.getId()), "data", "download");

				try
				{
					FileUtils.deleteDirectory(target);
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}

				// Delete the fileresource type. This will trigger the deletion of the referencing fileresouces.
				return record.delete() > 0;
			}
			else
			{
				return false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return false;
		}
	}
}
