package jhi.germinate.server.resource.images;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.records.DataExportJobsRecord;
import jhi.germinate.server.database.pojo.ExportJobDetails;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.ImageZipExporter;
import jhi.oddjob.JobInfo;
import org.jooq.*;

import java.io.File;
import java.io.*;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableImages.*;

@jakarta.ws.rs.Path("image/table/export")
@Secured
@PermitAll
public class ImageTableExportResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AsyncExportResult> postImageTableExport(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectJoinStep<Record> from = context.select().from(VIEW_TABLE_IMAGES);
			// Filter here!
			where(from, filters);
			List<Integer> imageIds = new ArrayList<>();

			setPaginationAndOrderBy(from)
				.forEach(i -> {
					imageIds.add(i.get(VIEW_TABLE_IMAGES.IMAGE_ID));
				});

			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = ResourceUtils.getFromExternal(resp, uuid, "async");
			asyncFolder.mkdirs();

			// Store the job information in the database
			DataExportJobsRecord dbJob = context.newRecord(DATA_EXPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId("N/A");
			dbJob.setDatatype(DataExportJobsDatatype.images);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setJobConfig(new ExportJobDetails()
				.setyIds(imageIds.toArray(new Integer[0]))
				.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL)));
			dbJob.setStatus(DataExportJobsStatus.waiting);
			if (userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			File libFolder = ResourceUtils.getLibFolder();
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add(libFolder.getAbsolutePath() + File.separator + "*");
			args.add(ImageZipExporter.class.getCanonicalName());
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
			args.add(Integer.toString(dbJob.getId()));

			JobInfo info = ApplicationListener.SCHEDULER.submit("ImageZipExporter", "java", args, asyncFolder.getAbsolutePath());

			// Store the job information in the database
			dbJob.setJobId(info.getId());
			dbJob.store();

			// Return the result
			AsyncExportResult result = new AsyncExportResult();
			result.setUuid(uuid);
			result.setStatus("waiting");
			return Collections.singletonList(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}
}
