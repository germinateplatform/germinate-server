package jhi.germinate.server.resource.backup;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.BackupResult;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.text.*;
import java.util.*;

@Path("backup")
public class BackupResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public Response getBackups()
			throws IOException, SQLException
	{
		File backups = ResourceUtils.getFromExternal(resp, "backups");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		List<BackupResult> result = new ArrayList<>();
		if (backups != null && backups.exists() && backups.isDirectory())
		{
			File[] files = backups.listFiles(fn -> fn.getName().endsWith(".zip"));

			result = Arrays.stream(files).map(f -> {
							   String filename = f.getName();
							   String[] parts = filename.replace(".zip", "").split("_");

							   if (parts.length != 4)
								   return null;

							   try
							   {
								   return new BackupResult()
										   .setFilename(filename)
										   .setFilesize(f.length())
										   .setTimestamp(new Timestamp(sdf.parse(parts[0] + " " + parts[1]).getTime()))
										   .setType(Database.BackupType.valueOf(parts[2].toUpperCase()))
										   .setGerminateVersion(parts[3]);
							   }
							   catch (ParseException e)
							   {
								   return null;
							   }
						   }).filter(Objects::nonNull)
						   .toList();
		}

		return Response.ok(result).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public Response putBackup()
	{
		File zipFile = Database.attemptDatabaseDump(Database.BackupType.MANUAL);

		return Response.ok(zipFile != null && zipFile.exists()).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileResourceDownload(BackupResult backup)
			throws IOException
	{
		File zipFile = ResourceUtils.getFromExternal(resp, backup.getFilename(), "backups");

		if (zipFile != null)
			return Response.ok(zipFile.delete()).build();
		else
			return Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Path("/download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public Response getFileResourceDownload(@QueryParam("filename") String filename, @QueryParam("token") String token)
			throws IOException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.FULL && StringUtils.isEmpty(token))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		if (StringUtils.isEmpty(filename))
			return Response.status(Response.Status.BAD_REQUEST).build();

		// IMPORTANT: This needs to be here, because we are using a specific URL token to fetch this
		AuthenticationFilter.UserDetails userDetails = AuthenticationFilter.getDetailsFromUrlToken(token);
		if (!StringUtils.isEmpty(token) && userDetails == null)
			return Response.status(Response.Status.FORBIDDEN).build();

		File zipFile = ResourceUtils.getFromExternal(resp, filename, "backups");

		if (zipFile != null)
		{
			java.nio.file.Path filePath = zipFile.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
						   })
						   .type("application/zip")
						   .header("content-disposition", "attachment; filename=\"" + zipFile.getName() + "\"")
						   .header("content-length", zipFile.length())
						   .build();
		}
		else
		{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
