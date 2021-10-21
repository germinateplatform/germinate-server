package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.resource.UuidRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.DatasetExportJobs;
import jhi.germinate.server.database.codegen.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;

@Path("dataset/export/async")
public class AsyncDatasetExportResource extends ContextResource implements AsyncResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<DatasetExportJobs> postJson(UuidRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.selectFrom(DATASET_EXPORT_JOBS)
						  .where(DATASET_EXPORT_JOBS.UUID.in(request.getUuids())
														 .or(DATASET_EXPORT_JOBS.USER_ID.eq(userDetails.getId())))
						  .and(DATASET_EXPORT_JOBS.VISIBILITY.eq(true))
						  .orderBy(DATASET_EXPORT_JOBS.UPDATED_ON.desc())
						  .fetchInto(DatasetExportJobs.class);
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

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS)
													.where(DATASET_EXPORT_JOBS.UUID.in(jobUuid))
													.fetchAnyInto(DatasetExportJobsRecord.class);

			boolean isCancelRequest = record.getStatus() == DatasetExportJobsStatus.running;

			// If the user is logged in
			if (userDetails.getId() != -1000)
			{
				if (Objects.equals(record.getUserId(), userDetails.getId()))
				{
					if (isCancelRequest)
					{
						record.setStatus(DatasetExportJobsStatus.cancelled);
						cancelJob(record.getUuid(), record.getJobId());
					}
					record.setVisibility(false);
					record.store();
					return true;
				}
				else
				{
					resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
					return false;
				}
			}
			else
			{
				if (isCancelRequest)
				{
					record.setStatus(DatasetExportJobsStatus.cancelled);
					cancelJob(record.getUuid(), record.getJobId());
				}
				record.setVisibility(false);
				record.store();
				return true;
			}
		}
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
			DatasetExportJobsRecord record = context.selectFrom(DATASET_EXPORT_JOBS)
													.where(DATASET_EXPORT_JOBS.UUID.eq(jobUuid))
													.and(DATASET_EXPORT_JOBS.VISIBILITY.eq(true))
													.fetchAnyInto(DatasetExportJobsRecord.class);

			if (record == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			String uuid = record.getUuid();
			File jobFolder = ResourceUtils.getFromExternal(uuid, "async");

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
			record.store(DATASET_EXPORT_JOBS.VISIBILITY);

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
