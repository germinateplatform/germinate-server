package jhi.germinate.server.resource.datasets.export;

import org.jooq.Result;
import org.jooq.*;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.GenotypeExporter;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Groupmembers.*;
import static jhi.germinate.server.database.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExportResource extends BaseServerResource
{

	@Post("json")
	public AsyncExportResult postJson(SubsettedGenotypeDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()) || request.getMapId() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetsForUser(getRequest(), getResponse())
															  .stream()
															  .map(ViewTableDatasets::getDatasetId)
															  .collect(Collectors.toList());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn);)
		{
			Set<String> germplasmNames = getGermplasmNames(context, request);
			Set<String> markerNames = getMarkerNames(context, request);

			File sharedMapFile = createTempFile("map-" + request.getMapId(), "map");
			try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sharedMapFile), StandardCharsets.UTF_8))))
			{
				bw.write("# fjFile = MAP" + CRLF);
				SelectConditionStep<Record3<String, String, Double>> query = context.select(MARKERS.MARKER_NAME, MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START)
																					.from(MAPDEFINITIONS)
																					.leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
																					.where(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()));

				if (!CollectionUtils.isEmpty(markerNames))
					query.and(MARKERS.MARKER_NAME.in(markerNames));

				Result<Record3<String, String, Double>> mapResult = query.fetch();

				exportToFile(bw, mapResult, false);
			}

			String uuid = UUID.randomUUID().toString();
			List<ViewTableDatasets> ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), getRequest(), getResponse(), true);

			if (CollectionUtils.isEmpty(ds))
				return null;

			// Create all temporary files
			File hdf5 = getFromExternal(ds.get(0).getSourceFile(), "data", "genotypes");
			File output = createTempFile(uuid, "result", "flapjack");
			File tabbed = createTempFile(uuid, "tabbed", "txt");
			File mapFile = createTempFile(uuid, "map-" + request.getMapId(), "map");
			File headerFile = createTempFile(uuid, "header", "header");
			Files.copy(sharedMapFile.toPath(), mapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			File germplasmFile = null;
			if (!CollectionUtils.isEmpty(germplasmNames))
			{
				germplasmFile = createTempFile(uuid, "germplasm", "txt");
				Files.write(germplasmFile.toPath(), new ArrayList<>(germplasmNames), Charset.defaultCharset());
			}

			File markerFile = null;
			if (!CollectionUtils.isEmpty(markerNames))
			{
				markerFile = createTempFile(uuid, "markers", "txt");
				Files.write(markerFile.toPath(), new ArrayList<>(markerNames), Charset.defaultCharset());
			}
			Files.write(headerFile.toPath(), getFlapjackHeaders(getRequest()), Charset.defaultCharset());

			File libFolder = getLibFolder();
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add(libFolder.getAbsolutePath() + File.separator + "*");
			args.add(GenotypeExporter.class.getCanonicalName());
			args.add(hdf5.getAbsolutePath());
			args.add(mapFile.getAbsolutePath());
			args.add(tabbed.getAbsolutePath());
			args.add(germplasmFile != null ? germplasmFile.getAbsolutePath() : "''");
			args.add(markerFile != null ? markerFile.getAbsolutePath() : "''");
			args.add(headerFile != null ? headerFile.getAbsolutePath() : "''");
			args.add(output.getAbsolutePath());

			ApplicationListener.SCHEDULER.initialize();
			String jobId = ApplicationListener.SCHEDULER.submit("java", args, getTempDir(uuid).getAbsolutePath());

			// Store the job information in the database
			DatasetExportJobsRecord dbJob = context.newRecord(DATASET_EXPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(jobId);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setExperimentTypeId(1);
			dbJob.setStatus(DatasetExportJobsStatus.running);
			if (userDetails != null && userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			// Return the result
			AsyncExportResult result = new AsyncExportResult();
			result.setUuid(uuid);
			result.setStatus("running");
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private List<String> getFlapjackHeaders(Request request)
	{
		String serverBase = PropertyWatcher.get(ServerProperty.GERMINATE_CLIENT_URL);

		List<String> result = new ArrayList<>();

		if (!StringUtils.isEmpty(serverBase))
		{
			if (serverBase.endsWith("/"))
				serverBase = serverBase.substring(0, serverBase.length() - 1);
			result.add("# fjDatabaseLineSearch = " + serverBase + "/data/germplasm/$LINE");
			result.add("# fjDatabaseGroupPreview = " + serverBase + "/$GROUP"); // TODO
			result.add("# fjDatabaseGroupUpload = " + serverBase + "/"); // TODO
			result.add("# fjDatabaseMarkerSearch = " + serverBase + "/genotypes/marker/$MARKER");
		}

		return result;
	}

	private Set<String> getMarkerNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
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

			// Only keep those that are actually on the map
			result.retainAll(context.selectDistinct(MARKERS.MARKER_NAME)
									.from(MARKERS)
									.leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID))
									.where(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()))
									.fetchInto(String.class));

			return result;
		}
	}

	private Set<String> getGermplasmNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
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
