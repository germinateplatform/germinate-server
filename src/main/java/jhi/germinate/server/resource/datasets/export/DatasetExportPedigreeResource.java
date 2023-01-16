package jhi.germinate.server.resource.datasets.export;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.ExportJobDetails;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.PedigreeExporter;
import jhi.oddjob.JobInfo;
import org.jooq.*;

import java.io.File;
import java.io.*;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;

@Path("dataset/export/pedigree-async")
@Secured
@PermitAll
public class DatasetExportPedigreeResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AsyncExportResult> postJson(PedigreeRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "pedigree");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			return new ArrayList<>();

		List<AsyncExportResult> result = new ArrayList<>();
		try (Connection conn = Database.getConnection(true))
		{
			DSLContext context = Database.getContext(conn);
			for (Integer id : datasetIds)
			{
				ViewTableDatasets ds = DatasetTableResource.getDatasetForId(id, req, userDetails, true);

				if (ds == null)
					return null;

				String uuid = UUID.randomUUID().toString();

				// Get the target folder for all generated files
				File asyncFolder = ResourceUtils.getFromExternal(resp, uuid, "async");
				asyncFolder.mkdirs();

				Integer[] array = {ds.getDatasetId()};

				// Store the job information in the database
				DataExportJobsRecord dbJob = context.newRecord(DATA_EXPORT_JOBS);
				dbJob.setUuid(uuid);
				dbJob.setJobId("N/A");
				dbJob.setDatasetIds(array);
				dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				dbJob.setDatatype(DataExportJobsDatatype.pedigree);
				dbJob.setJobConfig(new ExportJobDetails()
					.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
					.setxIds(request.getxIds())
					.setxGroupIds(request.getxGroupIds())
					.setyIds(request.getyIds())
					.setyGroupIds(request.getyGroupIds())
					.setExportParams(request.getIncludeAttributes() ? new String[]{"includeAttributes"} : null));
				dbJob.setStatus(DataExportJobsStatus.waiting);
				if (userDetails.getId() != -1000)
					dbJob.setUserId(userDetails.getId());
				dbJob.store();

				File libFolder = ResourceUtils.getLibFolder();
				List<String> args = new ArrayList<>();
				args.add("-cp");
				args.add(libFolder.getAbsolutePath() + File.separator + "*");
				args.add(PedigreeExporter.class.getCanonicalName());
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
				args.add(Integer.toString(dbJob.getId()));

				JobInfo info = ApplicationListener.SCHEDULER.submit("GerminatePedigreeExporter", "java", args, asyncFolder.getAbsolutePath());

				// Store the job information in the database
				dbJob.setJobId(info.getId());
				dbJob.store();

				DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
				access.setDatasetId(ds.getDatasetId());
				access.setUserId(userDetails.getId());
				access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				access.store();

				// Return the result
				AsyncExportResult individualResult = new AsyncExportResult();
				individualResult.setUuid(uuid);
				individualResult.setStatus("waiting");

				result.add(individualResult);
			}

			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}
}
