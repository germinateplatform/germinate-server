package jhi.germinate.server.resource.datasets.export;

import com.google.gson.JsonArray;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.tables.Germinatebase;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.Result;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExportResource extends BaseServerResource
{
	@Post("json")
	public List<AsyncExportResult> postJson(SubsettedGenotypeDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			return new ArrayList<>();

		List<AsyncExportResult> result = new ArrayList<>();
		try (DSLContext context = Database.getContext())
		{
			for (Integer id : datasetIds)
			{
				ViewTableDatasets ds = DatasetTableResource.getDatasetForId(id, getRequest(), getResponse(), true);

				if (ds == null)
					return null;

				Set<String> germplasmNames = getGermplasmNames(context, request);
				Set<String> markerNames = getMarkerNames(context, request);

				String dsName = "dataset-" + ds.getDatasetId();

				if (request.getMapId() != null)
					dsName += "-map-" + request.getMapId();

				String uuid = UUID.randomUUID().toString();

				// Get the target folder for all generated files
				File asyncFolder = getFromExternal(uuid, "async");
				asyncFolder.mkdirs();

				File sharedMapFile;

				if (request.getMapId() != null)
				{
					sharedMapFile = createTempFile("map-" + request.getMapId(), "map");

					try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sharedMapFile), StandardCharsets.UTF_8))))
					{
						bw.write("# fjFile = MAP" + CRLF);
						SelectConditionStep<Record3<String, String, Double>> query = context.select(MARKERS.MARKER_NAME, MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START)
																							.from(MAPDEFINITIONS)
																							.leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
																							.leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MARKERS.ID).and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1)))
																							.where(DATASETMEMBERS.DATASET_ID.eq(id))
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
				File hdf5 = getFromExternal(ds.getSourceFile(), "data", "genotypes");

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
				File identifierFile = new File(asyncFolder, dsName + ".identifiers");
				writeIdentifiersFile(context, identifierFile, germplasmNames, id);
				File headerFile = new File(asyncFolder, dsName + ".header");
				Files.write(headerFile.toPath(), getFlapjackHeaders(getRequest()), StandardCharsets.UTF_8);

				File libFolder = getLibFolder();
				List<String> args = new ArrayList<>();
				args.add("-cp");
				args.add(libFolder.getAbsolutePath() + File.separator + "*");
				args.add(GenotypeExporter.class.getCanonicalName());
				args.add(hdf5.getAbsolutePath());
				args.add(asyncFolder.getAbsolutePath());
				args.add(dsName);
				List<String> formats = new ArrayList<>();
				if (request.isGenerateFlapjackProject())
					formats.add(AdditionalExportFormat.flapjack.name());
				if (request.isGenerateHapMap())
					formats.add(AdditionalExportFormat.hapmap.name());
				if (formats.size() > 0)
					args.add(String.join(",", formats));
				else
					args.add("\"\"");

				String jobId = ApplicationListener.SCHEDULER.submit("java", args, asyncFolder.getAbsolutePath());

				JsonArray array = new JsonArray(1);
				array.add(ds.getDatasetId());

				// Store the job information in the database
				DatasetExportJobsRecord dbJob = context.newRecord(DATASET_EXPORT_JOBS);
				dbJob.setUuid(uuid);
				dbJob.setJobId(jobId);
				dbJob.setDatasetIds(array);
				dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				dbJob.setDatasettypeId(1);
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
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	static List<String> getFlapjackHeaders(Request request)
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

	static Set<String> getMarkerNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
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

	static Set<String> getGermplasmNames(DSLContext context, SubsettedGenotypeDatasetRequest request)
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

	static void writeIdentifiersFile(DSLContext context, File targetFile, Set<String> germplasmNames, Integer datasetId)
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)))
		{
			bw.write("# fjFile = PHENOTYPE");
			bw.newLine();
			bw.write("\tPUID\tSource Material Name\tSource Material PUID\tSynonyms");

			Germinatebase g = GERMINATEBASE.as("g");
			Field<String> childName = GERMINATEBASE.NAME.as("childName");
			Field<String> childPuid = GERMINATEBASE.PUID.as("childPuid");
			Field<String> parentName = g.NAME.as("parentName");
			Field<String> parentPuid = g.PUID.as("parentPuid");
			Field<JsonArray> synonyms = SYNONYMS.SYNONYMS_.as("synonyms");

			SelectJoinStep<Record5<String, String, String, String, JsonArray>> step = context.select(
				childName,
				childPuid,
				parentName,
				parentPuid,
				synonyms
			).from(GERMINATEBASE.leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
			.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID))));

			// Restrict to the requested germplasm (if any)
			if (!CollectionUtils.isEmpty(germplasmNames))
				step.where(GERMINATEBASE.NAME.in(germplasmNames));
				// Otherwise, restrict it to everything in this dataset
			else
				step.where(DSL.exists(DSL.selectOne()
										 .from(DATASETMEMBERS)
										 .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID)
																		 .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
																		 .and(DATASETMEMBERS.DATASET_ID.eq(datasetId)))));

			// Get only the ones where there's either an entity parent or the PUID isn't null, otherwise we're wasting space in the file
			step.where(GERMINATEBASE.ENTITYPARENT_ID.isNotNull().or(GERMINATEBASE.PUID.isNotNull()));

			step.forEach(r -> {
				try
				{
					bw.newLine();
					bw.write(r.get(childName) + "\t");
					bw.write((r.get(childPuid) == null ? "" : r.get(childPuid)) + "\t");
					bw.write((r.get(parentName) == null ? "" : r.get(parentName)) + "\t");
					bw.write((r.get(parentPuid) == null ? "" : r.get(parentPuid)) + "\t");
					bw.write((r.get(synonyms)) == null ? "" : r.get(synonyms).toString());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
