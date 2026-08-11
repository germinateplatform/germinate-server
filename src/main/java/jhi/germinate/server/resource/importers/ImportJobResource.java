package jhi.germinate.server.resource.importers;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DataImportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.DataImportJobs;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.export.AsyncResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.DATA_IMPORT_JOBS;

@Path("import/template")
@Secured
public class ImportJobResource extends ContextResource implements AsyncResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<DataImportJobs> postImportJob(UuidRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectConditionStep<?> step = context.selectFrom(DATA_IMPORT_JOBS)
			                                     .where(DATA_IMPORT_JOBS.VISIBILITY.eq(true));

			if (userDetails.getId() != -1000)
				step.and(DATA_IMPORT_JOBS.USER_ID.eq(userDetails.getId()));
			else
				step.and(DATA_IMPORT_JOBS.UUID.in(request.getUuids()));

			return step.orderBy(DATA_IMPORT_JOBS.UPDATED_ON.desc())
			           .fetchInto(DataImportJobs.class);
		}
	}

	@DELETE
	@Path("/{jobUuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteImportJob(@PathParam("jobUuid") String jobUuid)
			throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (StringUtils.isEmpty(jobUuid))
			throw new BadRequestException();

		boolean result = false;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataImportJobsRecord record = context.selectFrom(DATA_IMPORT_JOBS)
			                                     .where(DATA_IMPORT_JOBS.UUID.in(jobUuid))
			                                     .fetchAnyInto(DataImportJobsRecord.class);

			boolean isCancelRequest = record.getStatus() == DataImportJobsStatus.running;

			// If the user is logged in
			if (userDetails.getId() != -1000)
			{
				if (Objects.equals(record.getUserId(), userDetails.getId()))
				{
					if (isCancelRequest)
					{
						record.setStatus(DataImportJobsStatus.cancelled);
						cancelJob(record.getUuid(), record.getJobId());
					}
					record.setVisibility(false);
					record.store();
					result = true;
				}
				else
					throw new ForbiddenException();
			}
			else
			{
				if (isCancelRequest)
				{
					record.setStatus(DataImportJobsStatus.cancelled);
					cancelJob(record.getUuid(), record.getJobId());
				}
				record.setVisibility(false);
				record.store();
				result = true;
			}

			// Delete the async folder corresponding to the job uuid.
			File asyncFolder = ResourceUtils.getFromExternal(record.getUuid(), "async");
			if (asyncFolder != null && asyncFolder.exists() && asyncFolder.isDirectory())
			{
				org.apache.commons.io.FileUtils.deleteDirectory(asyncFolder);
			}
		}

		return result;
	}

	@GET
	@Path("/{jobUuid}/import")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public List<AsyncExportResult> getImportJob(@PathParam("jobUuid") String jobUuid)
	{
		if (StringUtils.isEmpty(jobUuid))
			return new ArrayList<>();

		return new DataImportRunner().importData(jobUuid);
	}

	@GET
	@Path("/{jobUuid}/log")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	@Secured(UserType.DATA_CURATOR)
	public StreamingOutput getImportJobLog(@PathParam("jobUuid") String jobUuid, @Context HttpServletResponse response)
			throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataImportJobsRecord record = context.selectFrom(DATA_IMPORT_JOBS)
			                                     .where(DATA_IMPORT_JOBS.UUID.in(jobUuid))
			                                     .and(DATA_IMPORT_JOBS.VISIBILITY.eq(true))
			                                     .and(DATA_IMPORT_JOBS.STATUS.eq(DataImportJobsStatus.failed))
			                                     .fetchAnyInto(DataImportJobsRecord.class);

			if (record == null)
				throw new NotFoundException();

			File asyncFolder = ResourceUtils.getFromExternal(record.getUuid(), "async");

			if (asyncFolder == null || !asyncFolder.exists())
				throw new NotFoundException();

			try (Stream<java.nio.file.Path> stream = Files.find(asyncFolder.toPath(), 1, (path, basicFileAttributes) -> path.toFile().getName().matches(".+\\.(e|o)\\d+")))
			{
				List<File> logFiles = stream.map(java.nio.file.Path::toFile).collect(Collectors.toList());

				File zipFile = ResourceUtils.createTempFile(null, "log-" + record.getUuid(), ".zip", false);

				FileUtils.zipUp(zipFile, logFiles, false);

				return toStreamingResult(zipFile, "application/zip", response);
			}
		}
	}
}
