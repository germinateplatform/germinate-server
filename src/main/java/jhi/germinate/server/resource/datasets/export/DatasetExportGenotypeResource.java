package jhi.germinate.server.resource.datasets.export;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
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
import jhi.germinate.server.util.async.GenotypeExporter;
import jhi.oddjob.JobInfo;
import org.jooq.*;

import java.io.File;
import java.io.*;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

@Path("dataset/export/genotype")
@Secured
@PermitAll
public class DatasetExportGenotypeResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AsyncExportResult> postJson(SubsettedGenotypeDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "genotype", request.getDatasetIds(), true);

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
				dbJob.setDatatype(DataExportJobsDatatype.genotype);
				dbJob.setJobConfig(new ExportJobDetails()
					.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
					.setXIds(request.getXIds())
					.setXGroupIds(request.getXGroupIds())
					.setYIds(request.getYIds())
					.setYGroupIds(request.getYGroupIds())
					.setSubsetId(request.getMapId())
					.setFileHeaders(String.join("\n", getFlapjackHeaders()) + "\n")
					.setFileTypes(request.getFileTypes()));
				dbJob.setStatus(DataExportJobsStatus.waiting);
				if (userDetails.getId() != -1000)
					dbJob.setUserId(userDetails.getId());
				dbJob.store();

				File libFolder = ResourceUtils.getLibFolder();
				List<String> args = new ArrayList<>();
				args.add("-cp");
				args.add(libFolder.getAbsolutePath() + File.separator + "*");
				args.add(GenotypeExporter.class.getCanonicalName());
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
				args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
				args.add(Integer.toString(dbJob.getId()));

				JobInfo info = ApplicationListener.SCHEDULER.submit("GerminateGenotypeExporter", "java", args, asyncFolder.getAbsolutePath());

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

	static List<String> getFlapjackHeaders()
	{
		String clientBase = PropertyWatcher.get(ServerProperty.GERMINATE_CLIENT_URL);

		List<String> result = new ArrayList<>();

		if (!StringUtils.isEmpty(clientBase))
		{
			if (clientBase.endsWith("/"))
				clientBase = clientBase.substring(0, clientBase.length() - 1);
			result.add("# fjDatabaseLineSearch = " + clientBase + "/#/data/germplasm/$LINE");
			result.add("# fjDatabaseGroupPreview = " + clientBase + "/#/groups/upload/$GROUP");
			result.add("# fjDatabaseMarkerSearch = " + clientBase + "/#/data/genotypes/marker/$MARKER");
			result.add("# fjDatabaseGroupUpload = " + clientBase + "/api/group/upload");
		}

		return result;
	}

	static Set<String> getMarkerNameList(DSLContext context, SubsettedGenotypeDatasetRequest request)
	{
		if (request.getXGroupIds() == null && request.getXIds() == null)
		{
			return null;
		}
		else
		{
			Set<String> result = new LinkedHashSet<>();
			if (!CollectionUtils.isEmpty(request.getXIds()))
			{
				result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
									 .from(MARKERS)
									 .where(MARKERS.ID.in(request.getXIds()))
									 .fetchInto(String.class));
			}

			if (!CollectionUtils.isEmpty(request.getXGroupIds()))
			{
				result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
									 .from(MARKERS)
									 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(MARKERS.ID))
									 .where(GROUPMEMBERS.GROUP_ID.in(request.getXGroupIds()))
									 .fetchInto(String.class));
			}

			if (request.getMapId() != null)
			{
				// Only keep those that are actually on the map
				result.retainAll(context.selectDistinct(MARKERS.MARKER_NAME)
										.from(MARKERS)
										.leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID))
										.where(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()))
										.fetchInto(String.class));
			}

			return result;
		}
	}

	static Set<String> getGermplasmNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
	{
		if (request.getYGroupIds() == null && request.getYIds() == null)
		{
			return null;
		}
		else
		{
			Set<String> result = new LinkedHashSet<>();

			if (!CollectionUtils.isEmpty(request.getYIds()))
			{
				result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
									 .from(GERMINATEBASE)
									 .where(GERMINATEBASE.ID.in(request.getYIds()))
									 .fetchInto(String.class));
			}
			if (!CollectionUtils.isEmpty(request.getYGroupIds()))
			{
				result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
									 .from(GERMINATEBASE)
									 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
									 .where(GROUPMEMBERS.GROUP_ID.in(request.getYGroupIds()))
									 .fetchInto(String.class));
			}

			return result;
		}
	}
}
