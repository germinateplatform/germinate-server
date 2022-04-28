package jhi.germinate.server.resource.fileresource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableFileresources;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.UUID;

import static jhi.germinate.server.database.codegen.tables.Fileresources.*;
import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.*;

@Path("fileresource")
public class FileResourceResource extends ContextResource
{
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean putFileResource(ViewTableFileresources fileResource)
		throws IOException, SQLException
	{
		if (fileResource == null || fileResource.getFileresourceId() != null || fileResource.getFileresourcetypeId() == null || StringUtils.isEmpty(fileResource.getFileresourcePath()) || StringUtils.isEmpty(fileResource.getFileresourceName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			FileresourcetypesRecord type = context.selectFrom(FILERESOURCETYPES)
												  .where(FILERESOURCETYPES.ID.eq(fileResource.getFileresourcetypeId()))
												  .fetchAny();

			// Get the file reference from the tmp directory
			File source = new File(new File(System.getProperty("java.io.tmpdir")), fileResource.getFileresourcePath());

			// If the type doesn't exist or the source file isn't available fail
			if (type == null || !source.exists() || !source.isFile())
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			// Get the target location for this file
			File target = ResourceUtils.getFromExternal(resp, fileResource.getFileresourcePath(), "data", "download", Integer.toString(type.getId()));
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
				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return false;
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
	}


	@GET
	@Path("/{fileResourceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	@Secured
	@PermitAll
	public Response getFileResource(@PathParam("fileResourceId") Integer fileResourceId)
		throws IOException, SQLException
	{
		if (fileResourceId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			FileresourcesRecord record = context.selectFrom(FILERESOURCES)
												.where(FILERESOURCES.ID.eq(fileResourceId))
												.fetchAny();

			if (record == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			File resultFile = ResourceUtils.getFromExternal(resp, record.getPath(), "data", "download", Integer.toString(record.getFileresourcetypeId()));

			if (!resultFile.exists() || !resultFile.isFile())
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			String filename = resultFile.getName();

			String type = Files.probeContentType(resultFile.toPath());
			filename = record.getName().replaceAll("[^a-zA-Z0-9-_.]", "-") + filename.substring(filename.lastIndexOf("."));

			if (StringUtils.isEmpty(type))
				type = "*/*";

			return Response.ok(resultFile)
						   .type(type)
						   .header("content-disposition", "attachment;filename= \"" + filename + "\"")
						   .header("content-length", resultFile.length())
						   .build();
		}
	}

	@DELETE
	@Path("/{fileResourceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean deleteFileResource(@PathParam("fileResourceId") Integer fileResourceId)
		throws IOException, SQLException
	{
		if (fileResourceId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			FileresourcesRecord fileResource = context.selectFrom(FILERESOURCES)
													  .where(FILERESOURCES.ID.eq(fileResourceId))
													  .fetchAny();

			if (fileResource != null)
			{
				String path = fileResource.getPath();

				if (!StringUtils.isEmpty(path))
				{
					File file = ResourceUtils.getFromExternal(resp, path, "data", "download", Integer.toString(fileResource.getFileresourcetypeId()));

					if (file.exists() && file.isFile())
						file.delete();
				}

				return fileResource.delete() > 0;
			}

			return false;
		}
	}
}
