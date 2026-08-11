package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.FileresourcetypesRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.apache.commons.io.FileUtils;
import org.jooq.*;

import java.io.*;
import java.io.File;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.FILERESOURCETYPES;
import static jhi.germinate.server.database.codegen.tables.ViewTableFileresourcetypes.VIEW_TABLE_FILERESOURCETYPES;

@Path("fileresourcetype")
@Secured
@PermitAll
public class FileResourceTypeResource extends ContextResource
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableFileresourcetypes> getFileResourceType()
			throws SQLException
	{
		// TODO: Check if these need to be checked
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectWhereStep<?> step = context.selectFrom(VIEW_TABLE_FILERESOURCETYPES);

			return step.fetchInto(ViewTableFileresourcetypes.class);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public Integer postFileResource(Fileresourcetypes type)
			throws SQLException
	{
		if (type == null || StringUtils.isEmpty(type.getName()) || type.getId() != null)
			throw new BadRequestException();

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
			throws SQLException, StatusException
	{
		if (fileResourceTypeId == null)
			throw new BadRequestException();
		;

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
				File target = ResourceUtils.getFromExternal(Integer.toString(record.getId()), "data", "download");

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
			throw new InternalServerErrorException();
		}
	}
}
