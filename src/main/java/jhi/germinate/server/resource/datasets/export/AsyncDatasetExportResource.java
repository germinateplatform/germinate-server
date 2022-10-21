package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.resource.UuidRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DataExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.DataExportJobs;
import jhi.germinate.server.database.codegen.tables.records.DataExportJobsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;

@Path("dataset/export/async")
public class AsyncDatasetExportResource extends ContextResource implements AsyncResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<DataExportJobs> postJson(UuidRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectFrom(DATA_EXPORT_JOBS)
						  .where(DATA_EXPORT_JOBS.UUID.in(request.getUuids())
														 .or(DATA_EXPORT_JOBS.USER_ID.eq(userDetails.getId())))
						  .and(DATA_EXPORT_JOBS.VISIBILITY.eq(true))
						  .orderBy(DATA_EXPORT_JOBS.UPDATED_ON.desc())
						  .fetchInto(DataExportJobs.class);
		}
	}

	@DELETE
	@Path("/{jobUuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public boolean deleteAsyncDatasetExport(@PathParam("jobUuid") String jobUuid)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (StringUtils.isEmpty(jobUuid))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		boolean result = false;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS)
													.where(DATA_EXPORT_JOBS.UUID.in(jobUuid))
													.fetchAnyInto(DataExportJobsRecord.class);

			boolean isCancelRequest = record.getStatus() == DataExportJobsStatus.running;

			// If the user is logged in
			if (userDetails.getId() != -1000)
			{
				if (Objects.equals(record.getUserId(), userDetails.getId()))
				{
					if (isCancelRequest)
					{
						record.setStatus(DataExportJobsStatus.cancelled);
						cancelJob(record.getUuid(), record.getJobId());
					}
					record.setVisibility(false);
					record.store();
					result = true;
				}
				else
				{
					resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
					result = false;
				}
			}
			else
			{
				if (isCancelRequest)
				{
					record.setStatus(DataExportJobsStatus.cancelled);
					cancelJob(record.getUuid(), record.getJobId());
				}
				record.setVisibility(false);
				record.store();
				result = true;
			}

			// Delete the async folder corresponding to the job uuid.
			File asyncFolder = ResourceUtils.getFromExternal(null, record.getUuid(), "async");
			if (asyncFolder != null && asyncFolder.exists() && asyncFolder.isDirectory()) {
				org.apache.commons.io.FileUtils.deleteDirectory(asyncFolder);
			}
		}

		return result;
	}

	@GET
	@Path("/{jobUuid}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response getJson(@PathParam("jobUuid") String jobUuid)
		throws IOException, SQLException
	{
		if (StringUtils.isEmpty(jobUuid))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS)
													.where(DATA_EXPORT_JOBS.UUID.eq(jobUuid))
													.and(DATA_EXPORT_JOBS.VISIBILITY.eq(true))
													.fetchAnyInto(DataExportJobsRecord.class);

			if (record == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			String uuid = record.getUuid();
			File jobFolder = ResourceUtils.getFromExternal(resp, uuid, "async");

			// Get zip result files (there'll only be one per folder)
			File[] zipFiles = jobFolder.listFiles((dir, name) -> name.endsWith(".zip"));

			if (CollectionUtils.isEmpty(zipFiles))
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			File resultFile = zipFiles[0];
			// Update this, so the file doesn't get deleted by the background async folder cleanup task
			resultFile.setLastModified(System.currentTimeMillis());

			record.setVisibility(false);
			record.store(DATA_EXPORT_JOBS.VISIBILITY);

			java.nio.file.Path zipFilePath = resultFile.toPath();
			return Response.ok((StreamingOutput) output -> {
				Files.copy(zipFilePath, output);
				// Delete the whole folder once we're done
				FileUtils.deleteDirectory(jobFolder);
			})
						   .type("application/zip")
						   .header("content-disposition", "attachment;filename= \"" + resultFile.getName() + "\"")
						   .header("content-length", resultFile.length())
						   .build();
		}
	}
}
