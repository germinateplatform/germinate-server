package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableFileresources;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
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
			record.setPath(target.getName());
			record.setFilesize(target.length());
			record.setDescription(fileResource.getFileresourceDescription());
			record.setFileresourcetypeId(type.getId());
			record.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			record.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			record.store();

			if (!CollectionUtils.isEmpty(fileResource.getDatasetIds()))
			{
				List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, fileResource.getDatasetIds(), true);

				for (Integer datasetId : requestedIds)
				{
					DatasetfileresourcesRecord fileRes = context.newRecord(DATASETFILERESOURCES);
					fileRes.setDatasetId(datasetId);
					fileRes.setFileresourceId(record.getId());
					fileRes.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					fileRes.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
					fileRes.store();
				}
			}

			return true;
		}
	}

	@GET
	@Path("/{fileResourceId:\\d+}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	public Response getFileResourceDownload(@PathParam("fileResourceId") Integer fileResourceId, @QueryParam("token") String token) throws IOException, SQLException {
		// IMPORTANT: This needs to be here, because we are using a specific URL token to fetch this
		AuthenticationFilter.UserDetails userDetails = AuthenticationFilter.getDetailsFromUrlToken(token);
		if (userDetails == null) {
			userDetails = new AuthenticationFilter.UserDetails(-1000, token, token, UserType.UNKNOWN, AuthenticationFilter.AGE);
		}
		return getFileResourceInternal(fileResourceId, userDetails);
	}

	@GET
	@Path("/{fileResourceId:[0-9]+}{fileExtension:.?[a-zA-Z]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	@Secured
	@PermitAll
	public Response getFileResource(@PathParam("fileResourceId") Integer fileResourceId)
		throws IOException, SQLException
	{
		return getFileResourceInternal(fileResourceId, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal());
	}

	private Response getFileResourceInternal(Integer fileResourceId, AuthenticationFilter.UserDetails userDetails)
			throws IOException, SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, true);

		if (fileResourceId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			// Check whether there isn't a dataset linked to this resource OR whether the user has access to that dataset
			Condition cond = DSL.notExists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID)))
								.or(DSL.exists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID).and(DATASETFILERESOURCES.DATASET_ID.in(datasetIds)))));
			DSLContext context = Database.getContext(conn);
			FileresourcesRecord record = context.selectFrom(FILERESOURCES)
												.where(FILERESOURCES.ID.eq(fileResourceId).and(cond))
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
	@Path("/{fileResourceId:\\d+}")
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
