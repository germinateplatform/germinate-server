package jhi.germinate.server.resource.datasets.export;

import com.google.gson.*;

import org.jooq.Result;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.tables.records.DatasetExportJobsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.*;

import static jhi.germinate.server.database.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.tables.Datasetmembers.*;
import static jhi.germinate.server.database.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class AlleleFrequencyExportResource extends BaseServerResource
{

	@Post("json")
	public AsyncExportResult postJson(AlleleFrequencyDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			List<ViewTableDatasets> ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), getRequest(), getResponse(), true);

			if (CollectionUtils.isEmpty(ds))
				return null;

			Set<String> germplasmNames = GenotypeExportResource.getGermplasmNames(context, request);
			Set<String> markerNames = GenotypeExportResource.getMarkerNames(context, request);

			String dsName = "dataset-" + ds.get(0).getDatasetId();

			if (request.getMapId() != null)
				dsName += "-map-" + request.getMapId();

			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = getFromExternal(uuid, "async");
			asyncFolder.mkdirs();

			File sharedMapFile;

			if (request.getMapId() != null) {
				sharedMapFile = createTempFile("map-" + request.getMapId(), "map");

				try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sharedMapFile), StandardCharsets.UTF_8))))
				{
					bw.write("# fjFile = MAP" + CRLF);
					SelectConditionStep<Record3<String, String, Double>> query = context.select(MARKERS.MARKER_NAME, MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START)
																						.from(MAPDEFINITIONS)
																						.leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
																						.leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MARKERS.ID).and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1)))
																						.where(DATASETMEMBERS.DATASET_ID.in(datasetIds))
																						.and(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()));

					if (!CollectionUtils.isEmpty(markerNames))
						query.and(MARKERS.MARKER_NAME.in(markerNames));

					Result<Record3<String, String, Double>> mapResult = query.fetch();

					exportToFile(bw, mapResult, false, null);
				}

				File mapFile = new File(asyncFolder, dsName + ".map");
				Files.copy(sharedMapFile.toPath(), mapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			// Get the source hdf5 file
			File hdf5 = getFromExternal(ds.get(0).getSourceFile(), "data", "allelefreq");

			// Create all temporary files
			if (!CollectionUtils.isEmpty(germplasmNames))
			{
				File germplasmFile = new File(asyncFolder, dsName + ".germplasm");
				Files.write(germplasmFile.toPath(), new ArrayList<>(germplasmNames), StandardCharsets.UTF_8);
			}

			if (!CollectionUtils.isEmpty(markerNames))
			{
				File markerFile = new File(asyncFolder, dsName + ".markers");
				Files.write(markerFile.toPath(), new ArrayList<>(markerNames), StandardCharsets.UTF_8);
			}
			File headerFile = new File(asyncFolder, dsName + ".header");
			Files.write(headerFile.toPath(), GenotypeExportResource.getFlapjackHeaders(), StandardCharsets.UTF_8);

			if (request.getConfig() != null) {
				File configFile = new File(asyncFolder, dsName + ".json");
				Files.write(configFile.toPath(), Collections.singleton(new Gson().toJson(request.getConfig())), StandardCharsets.UTF_8);
			}

			File libFolder = getLibFolder();
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add(libFolder.getAbsolutePath() + File.separator + "*");
			args.add(AllelefreqExporter.class.getCanonicalName());
			args.add(hdf5.getAbsolutePath());
			args.add(asyncFolder.getAbsolutePath());
			args.add(dsName);
			args.add(Boolean.toString(request.isGenerateFlapjackProject()));

			ApplicationListener.SCHEDULER.initialize();
			String jobId = ApplicationListener.SCHEDULER.submit("java", args, asyncFolder.getAbsolutePath());

			JsonArray array = new JsonArray(1);
			array.add(ds.get(0).getDatasetId());

			// Store the job information in the database
			DatasetExportJobsRecord dbJob = context.newRecord(DATASET_EXPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(jobId);
			dbJob.setDatasetIds(array);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setExperimentTypeId(4);
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
}
