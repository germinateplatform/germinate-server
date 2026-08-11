package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableFileresources;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.DATASETFILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.Fileresources.FILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.FILERESOURCETYPES;

@Path("fileresource")
public class FileResourceResource extends ContextResource
{
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean putFileResource(ViewTableFileresources fileResource)
			throws IOException, SQLException, StatusException
	{
		if (fileResource == null || fileResource.getFileresourceId() != null || fileResource.getFileresourcetypeId() == null || StringUtils.isEmpty(fileResource.getFileresourcePath()) || StringUtils.isEmpty(fileResource.getFileresourceName()))
			throw new BadRequestException();

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
				throw new BadRequestException();

			// Get the target location for this file
			File target = ResourceUtils.getFromExternal(fileResource.getFileresourcePath(), "data", "download", Integer.toString(type.getId()));
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
				throw new InternalServerErrorException();
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
	@Produces("*/*")
	public File getFileResourceDownload(@PathParam("fileResourceId") Integer fileResourceId, @QueryParam("token") String token, @Context HttpServletResponse response)
			throws IOException, SQLException, StatusException
	{
		// IMPORTANT: This needs to be here, because we are using a specific URL token to fetch this
		AuthenticationFilter.UserDetails userDetails = AuthenticationFilter.getDetailsFromUrlToken(token);
		if (userDetails == null)
		{
			userDetails = new AuthenticationFilter.UserDetails(-1000, token, token, UserType.UNKNOWN, AuthenticationFilter.AGE);
		}
		FileResult result = getFileResourceInternal(fileResourceId, userDetails);

		return toFileResult(result.file, result.mime, result.name, response);
	}

	@GET
	@Path("/{fileResourceId:[0-9]+}{fileExtension:.?[a-zA-Z]*}")
	@Produces("*/*")
	@Secured
	@PermitAll
	public File getFileResource(@PathParam("fileResourceId") Integer fileResourceId, @Context HttpServletResponse response)
			throws IOException, SQLException, StatusException
	{
		FileResult result = getFileResourceInternal(fileResourceId, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal());

		return toFileResult(result.file, result.mime, result.name, response);
	}

	private FileResult getFileResourceInternal(Integer fileResourceId, AuthenticationFilter.UserDetails userDetails)
			throws IOException, SQLException, StatusException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, true);

		if (fileResourceId == null)
			throw new StatusException(Response.Status.BAD_REQUEST.getStatusCode());

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
				throw new StatusException(Response.Status.NOT_FOUND.getStatusCode());

			File resultFile = ResourceUtils.getFromExternal(record.getPath(), "data", "download", Integer.toString(record.getFileresourcetypeId()));

			if (!resultFile.exists() || !resultFile.isFile())
				throw new StatusException(Response.Status.NOT_FOUND.getStatusCode());

			String filename = resultFile.getName();

			String type = Files.probeContentType(resultFile.toPath());
			filename = record.getName().replaceAll("[^a-zA-Z0-9-_.]", "-") + filename.substring(filename.lastIndexOf("."));

			if (StringUtils.isEmpty(type))
				type = "*/*";

			return new FileResult()
					.setFile(resultFile)
					.setName(filename)
					.setMime(type);
		}
	}


	@DELETE
	@Path("/{fileResourceId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean deleteFileResource(@PathParam("fileResourceId") Integer fileResourceId)
			throws IOException, SQLException, StatusException
	{
		if (fileResourceId == null)
			throw new BadRequestException();

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
					File file = ResourceUtils.getFromExternal(path, "data", "download", Integer.toString(fileResource.getFileresourcetypeId()));

					if (file.exists() && file.isFile())
						file.delete();
				}

				return fileResource.delete() > 0;
			}

			return false;
		}
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class FileResult {
		File file;
		String name;
		String mime;
	}
}
