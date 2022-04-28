package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.PedigreeExporter;
import jhi.oddjob.JobInfo;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

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

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "pedigree");

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
				ViewTableDatasets ds = DatasetTableResource.getDatasetForId(id, req, resp, userDetails, true);

				if (ds == null)
					return null;

				Set<String> germplasmNames = getGermplasmNames(context, request);

				String uuid = UUID.randomUUID().toString();

				// Get the target folder for all generated files
				File asyncFolder = ResourceUtils.getFromExternal(resp, uuid, "async");
				asyncFolder.mkdirs();

				// Create all temporary files
				if (!CollectionUtils.isEmpty(germplasmNames))
				{
					File germplasmFile = new File(asyncFolder, uuid + ".germplasm");
					Files.write(germplasmFile.toPath(), new ArrayList<>(germplasmNames), StandardCharsets.UTF_8);
				}

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
				args.add(CollectionUtils.join(request.getDatasetIds(), ","));
				args.add(asyncFolder.getAbsolutePath());
				args.add(request.getIncludeAttributes().toString());

				JobInfo info = ApplicationListener.SCHEDULER.submit("GerminatePedigreeExporter", "java", args, asyncFolder.getAbsolutePath());

				Integer[] array = {ds.getDatasetId()};

				// Store the job information in the database
				DatasetExportJobsRecord dbJob = context.newRecord(DATASET_EXPORT_JOBS);
				dbJob.setUuid(uuid);
				dbJob.setJobId(info.getId());
				dbJob.setDatasetIds(array);
				dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				dbJob.setDatasettypeId(7);
				dbJob.setStatus(DatasetExportJobsStatus.running);
				if (userDetails.getId() != -1000)
					dbJob.setUserId(userDetails.getId());
				dbJob.store();

				DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
				access.setDatasetId(ds.getDatasetId());
				access.setUserId(userDetails.getId());
				access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				access.store();

				// Return the result
				AsyncExportResult individualResult = new AsyncExportResult();
				individualResult.setUuid(uuid);
				individualResult.setStatus("running");

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
		if (request.getxGroupIds() == null && request.getxIds() == null)
		{
			return null;
		}
		else
		{
			Set<String> result = new LinkedHashSet<>();
			if (!CollectionUtils.isEmpty(request.getxIds()))
			{
				result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
									 .from(MARKERS)
									 .where(MARKERS.ID.in(request.getxIds()))
									 .fetchInto(String.class));
			}

			if (!CollectionUtils.isEmpty(request.getxGroupIds()))
			{
				result.addAll(context.selectDistinct(MARKERS.MARKER_NAME)
									 .from(MARKERS)
									 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(MARKERS.ID))
									 .where(GROUPMEMBERS.GROUP_ID.in(request.getxGroupIds()))
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

	static SelectConditionStep<Record1<String>> getMarkerNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
	{
		if (request.getxGroupIds() == null && request.getxIds() == null)
		{
			return null;
		}
		else
		{
			SelectConditionStep<Record1<String>> step = context.selectDistinct(MARKERS.MARKER_NAME)
															   .from(MARKERS)
															   .where(MARKERS.ID.in(request.getxIds())
																				.orExists(DSL.selectFrom(GROUPMEMBERS).where(GROUPMEMBERS.FOREIGN_ID.eq(MARKERS.ID).and(GROUPMEMBERS.GROUP_ID.in(request.getxGroupIds())))));

			if (request.getMapId() != null)
				step.andExists(DSL.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID).and(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()))));

			return step;
		}
	}

	static Set<String> getGermplasmNames(DSLContext context, PedigreeRequest request)
	{
		if (request.getyGroupIds() == null && request.getyIds() == null)
		{
			return null;
		}
		else
		{
			Set<String> result = new LinkedHashSet<>();

			if (!CollectionUtils.isEmpty(request.getyIds()))
			{
				result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
									 .from(GERMINATEBASE)
									 .where(GERMINATEBASE.ID.in(request.getyIds()))
									 .fetchInto(String.class));
			}
			if (!CollectionUtils.isEmpty(request.getyGroupIds()))
			{
				result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
									 .from(GERMINATEBASE)
									 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
									 .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
									 .fetchInto(String.class));
			}

			return result;
		}
	}
}
