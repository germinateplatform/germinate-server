package jhi.germinate.server.resource.backup;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;

import java.io.*;
import java.sql.Timestamp;
import java.text.*;
import java.util.*;

@Path("backup")
public class BackupResource extends BaseResource
{
	@POST
	@Path("/table")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public PaginatedResult<List<BackupResult>> postBackupTable(PaginatedRequest request)
			throws IOException, StatusException
	{
		processRequest(request);
		List<BackupResult> result = getBackupsInternally();

		if (!StringUtils.isEmpty(orderBy))
		{
			result.sort((a, b) -> {
				int sortResult = 0;
				switch (orderBy)
				{
					case "timestamp":
						sortResult = (int) Math.signum(a.getTimestamp().getTime() - b.getTimestamp().getTime());
						break;
					case "filename":
						sortResult = a.getFilename().compareTo(b.getFilename());
						break;
					case "germinateVersion":
						sortResult = a.getGerminateVersion().compareTo(b.getGerminateVersion());
						break;
					case "type":
						sortResult = a.getType().name().compareTo(b.getType().name());
						break;
					case "filesize":
						sortResult = (int) Math.signum(a.getFilesize() - b.getFilesize());
						break;
				}

				if (!ascending)
					sortResult = -sortResult;

				return sortResult;
			});
		}

		int count = result.size();
		result = result.subList(pageSize * currentPage, Math.min(pageSize * (currentPage + 1), count));

		return new PaginatedResult<>(result, count);
	}

	private List<BackupResult> getBackupsInternally()
			throws IOException, StatusException
	{
		File backups = ResourceUtils.getFromExternal("backups");

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

		return new ArrayList<>(result);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public List<BackupResult> getBackups()
			throws IOException, StatusException
	{
		return getBackupsInternally();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean putBackup()
	{
		File zipFile = Database.attemptDatabaseDump(Database.BackupType.MANUAL);

		return zipFile != null && zipFile.exists();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean getFileResourceDownload(BackupResult backup)
			throws IOException, StatusException
	{
		File zipFile = ResourceUtils.getFromExternal(backup.getFilename(), "backups");

		if (zipFile != null)
			return zipFile.delete();
		else
			throw new NotFoundException();
	}

	@GET
	@Path("/download")
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public StreamingOutput getFileResourceDownload(@QueryParam("filename") String filename, @QueryParam("token") String token, @Context HttpServletResponse response)
			throws IOException, StatusException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		if (mode == AuthenticationMode.FULL && StringUtils.isEmpty(token))
			throw new StatusException(Response.Status.UNAUTHORIZED.getStatusCode());
		if (StringUtils.isEmpty(filename))
			throw new BadRequestException();

		// IMPORTANT: This needs to be here, because we are using a specific URL token to fetch this
		AuthenticationFilter.UserDetails userDetails = AuthenticationFilter.getDetailsFromUrlToken(token);
		if (!StringUtils.isEmpty(token) && userDetails == null)
			throw new ForbiddenException();

		File zipFile = ResourceUtils.getFromExternal(filename, "backups");

		if (zipFile != null)
			return toStreamingResult(zipFile, "application/zip", response);
		else
			throw new NotFoundException();
	}
}
