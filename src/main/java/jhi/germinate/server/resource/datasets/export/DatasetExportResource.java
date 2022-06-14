package jhi.germinate.server.resource.datasets.export;

import com.google.gson.Gson;
import de.ipk_gatersleben.bit.bi.isa4j.components.*;
import jhi.flapjack.io.binning.MakeHistogram;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.codegen.routines.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.*;
import jhi.germinate.server.resource.pedigrees.PedigreeResource;
import jhi.germinate.server.resource.traits.TraitTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.*;
import jhi.oddjob.JobInfo;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

@Path("dataset/export")
@Secured
@PermitAll
public class DatasetExportResource extends ContextResource
{
	@POST
	@Path("/allelefreq")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AsyncExportResult postJson(AlleleFrequencyDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "allelefreq");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), req, resp, userDetails, true);

			if (ds == null)
				return null;

			Set<String> germplasmNames = DatasetExportGenotypeResource.getGermplasmNames(context, request);
			SelectConditionStep<Record1<String>> markerNames = DatasetExportGenotypeResource.getMarkerNames(context, request);

			String dsName = "dataset-" + ds.getDatasetId();

			if (request.getMapId() != null)
				dsName += "-map-" + request.getMapId();

			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = ResourceUtils.getFromExternal(resp, uuid, "async");
			asyncFolder.mkdirs();

			File sharedMapFile;

			if (request.getMapId() != null)
			{
				sharedMapFile = ResourceUtils.createTempFile("map-" + request.getMapId(), "map");

				SelectConditionStep<Record3<String, String, Double>> query = context.select(MARKERS.MARKER_NAME, MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START)
																					.from(MAPDEFINITIONS)
																					.leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
																					.leftJoin(DATASETMEMBERS).on(DATASETMEMBERS.FOREIGN_ID.eq(MARKERS.ID).and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1)))
																					.where(DATASETMEMBERS.DATASET_ID.in(datasetIds))
																					.and(MAPDEFINITIONS.MAP_ID.eq(request.getMapId()));

				try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sharedMapFile), StandardCharsets.UTF_8)));
					 Cursor<? extends Record> cursor = query.fetchLazy())
				{
					bw.write("# fjFile = MAP" + ResourceUtils.CRLF);

					ResourceUtils.exportToFileStreamed(bw, cursor, false, null);
				}

				File mapFile = new File(asyncFolder, dsName + ".map");
				Files.copy(sharedMapFile.toPath(), mapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			// Get the source hdf5 file
			File hdf5 = ResourceUtils.getFromExternal(resp, ds.getSourceFile(), "data", "allelefreq");

			// Create all temporary files
			if (!CollectionUtils.isEmpty(germplasmNames))
			{
				File germplasmFile = new File(asyncFolder, dsName + ".germplasm");
				Files.write(germplasmFile.toPath(), new ArrayList<>(germplasmNames), StandardCharsets.UTF_8);
			}

			if (markerNames != null)
			{
				java.nio.file.Path markerFile = new File(asyncFolder, dsName + ".markers").toPath();

				try (BufferedWriter bw = Files.newBufferedWriter(markerFile, StandardCharsets.UTF_8);
					 Cursor<? extends Record> cursor = markerNames.fetchLazy())
				{
					ResourceUtils.exportToFileStreamed(bw, cursor, false, null);
				}
			}
			File headerFile = new File(asyncFolder, dsName + ".header");
			Files.write(headerFile.toPath(), DatasetExportGenotypeResource.getFlapjackHeaders(), StandardCharsets.UTF_8);
			File identifierFile = new File(asyncFolder, dsName + ".identifiers");
			DatasetExportGenotypeResource.writeIdentifiersFile(context, identifierFile, germplasmNames, ds.getDatasetId());

			if (request.getConfig() != null)
			{
				File configFile = new File(asyncFolder, dsName + ".json");
				Files.write(configFile.toPath(), Collections.singleton(new Gson().toJson(request.getConfig())), StandardCharsets.UTF_8);
			}

			File libFolder = ResourceUtils.getLibFolder();
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add(libFolder.getAbsolutePath() + File.separator + "*");
			args.add(AllelefreqExporter.class.getCanonicalName());
			args.add(hdf5.getAbsolutePath());
			args.add(asyncFolder.getAbsolutePath());
			args.add(dsName);
			args.add(request.isGenerateFlapjackProject() ? AdditionalExportFormat.flapjack.name() : "\"\"");

			JobInfo info = ApplicationListener.SCHEDULER.submit("AlleleFrequencyGenotypeExporter", "java", args, asyncFolder.getAbsolutePath());

			Integer[] array = {ds.getDatasetId()};

			// Store the job information in the database
			DatasetExportJobsRecord dbJob = context.newRecord(DATASET_EXPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId(info.getId());
			dbJob.setDatasetIds(array);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatasettypeId(4);
			dbJob.setStatus(DatasetExportJobsStatus.running);
			if (userDetails != null && userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
			access.setDatasetId(ds.getDatasetId());
			access.setUserId(userDetails.getId());
			access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			access.store();

			// Return the result
			AsyncExportResult result = new AsyncExportResult();
			result.setUuid(uuid);
			result.setStatus("running");
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}

	@POST
	@Path("/allelefreq/histogram")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postJson(SubsettedGenotypeDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "allelefreq");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), req, resp, userDetails, true);

			if (ds == null)
				return null;

			Set<String> germplasmNames = DatasetExportGenotypeResource.getGermplasmNames(context, request);
			Set<String> markerNames = DatasetExportGenotypeResource.getMarkerNameList(context, request);

			// Get the source file
			File source = ResourceUtils.getFromExternal(resp, ds.getSourceFile(), "data", "allelefreq");

			// Create all temporary files
			File target = ResourceUtils.createTempFile("allelefreq-" + CollectionUtils.join(datasetIds, "-"), ".txt");

			int[] counts = new TabFileSubsetter().run(source, target, germplasmNames, markerNames, null);

			File histogram = ResourceUtils.createTempFile("allelefreq-histogram-" + CollectionUtils.join(datasetIds, "-"), ".txt");
			if (counts[0] == 0 || counts[1] == 0)
			{
				// If either dimension is empty (no markers or no germplasm), then just create a dummy histogram file, because otherwise
				// the Flapjack code will fail with a "divide by zero" error.
				Files.write(histogram.toPath(), Collections.singletonList("position\tcount"));
			}
			else
			{
				new MakeHistogram(200, target.getAbsolutePath(), histogram.getAbsolutePath()).createHistogram();
			}

			java.nio.file.Path filePath = histogram.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
							   Files.deleteIfExists(filePath);
						   })
						   .type(MediaType.TEXT_PLAIN)
						   .header("content-disposition", "attachment; filename=\"" + histogram.getName() + "\"")
						   .header("content-length", histogram.length())
						   .build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}

	@POST
	@Path("/climate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postDatasetExportClimate(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "climate");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try
		{
			File file = ResourceUtils.createTempFile("climate-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".tsv");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String climateIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				ExportClimateData procedure = new ExportClimateData();
				procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);
				if (!StringUtils.isEmpty(germplasmIdString))
					procedure.setMarkedids(germplasmIdString);
				if (!StringUtils.isEmpty(climateIdString))
					procedure.setClimateids(climateIdString);

				procedure.execute(context.configuration());

				ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);

				for (Integer dsId : datasetIds)
				{
					DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
					access.setDatasetId(dsId);
					access.setUserId(userDetails.getId());
					access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					access.store();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return null;
			}

			java.nio.file.Path filePath = file.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
							   Files.deleteIfExists(filePath);
						   })
						   .type(MediaType.TEXT_PLAIN)
						   .header("content-disposition", "attachment; filename=\"" + file.getName() + "\"")
						   .header("content-length", file.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}


	@POST
	@Path("/compound")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postDatasetExportCompound(SubsettedDatasetRequest request)
		throws SQLException, IOException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "compound");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try
		{
			File file = ResourceUtils.createTempFile("compound-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".tsv");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String traitIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				ExportCompoundData procedure = new ExportCompoundData();
				procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);
				if (!StringUtils.isEmpty(germplasmIdString))
					procedure.setMarkedids(germplasmIdString);
				if (!StringUtils.isEmpty(traitIdString))
					procedure.setCompoundids(traitIdString);

				procedure.execute(context.configuration());

				ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);

				for (Integer dsId : datasetIds)
				{
					DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
					access.setDatasetId(dsId);
					access.setUserId(userDetails.getId());
					access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					access.store();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return null;
			}

			java.nio.file.Path filePath = file.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
							   Files.deleteIfExists(filePath);
						   })
						   .type(MediaType.TEXT_PLAIN)
						   .header("content-disposition", "attachment; filename=\"" + file.getName() + "\"")
						   .header("content-length", file.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}


	@POST
	@Path("/trial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public Response postDatasetExportTrial(SubsettedDatasetRequest request, @QueryParam("format") String formatString)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "trials");

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		TrialsExportFormat format = TrialsExportFormat.tab;

		try
		{
			format = TrialsExportFormat.valueOf(formatString);
		}
		catch (Exception e)
		{
		}

		File file;
		String mediaType;

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			switch (format)
			{
				case isatab:
					file = exportIsaTab(request, context, datasetIds);
					mediaType = "application/zip";
					break;
				case tab:
				default:
					try
					{
						file = exportTab("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);
						mediaType = MediaType.TEXT_PLAIN;
					}
					catch (GerminateException e)
					{
						conn.close();
						resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
						return null;
					}
					break;
			}

			for (Integer dsId : datasetIds)
			{
				DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
				access.setDatasetId(dsId);
				access.setUserId(userDetails.getId());
				access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				access.store();
			}

			java.nio.file.Path filePath = file.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
							   Files.deleteIfExists(filePath);
						   })
						   .type(mediaType)
						   .header("content-disposition", "attachment; filename=\"" + file.getName() + "\"")
						   .header("content-length", file.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}

	@POST
	@Path("/pedigree")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public Response postDatasetExportPedigree(PedigreeRequest request)
		throws IOException, SQLException
	{
		return PedigreeResource.exportFlatFile(request, req, resp, securityContext);
	}


	private File exportIsaTab(SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
		throws IOException, SQLException
	{
		File zipFile = ResourceUtils.createTempFile(null, "trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".zip", false);
		List<File> resultFiles = new ArrayList<>();

		Investigation inv = new Investigation("Germinate");
		inv.setTitle("Germinate");
		inv.setDescription("This dataset contains phenotypic data exported from Germinate");

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		for (Integer dsId : datasetIds)
		{
			try
			{
				ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(dsId, req, resp, userDetails, true);

				if (dataset == null)
					continue;

				Study study = new Study(Integer.toString(dataset.getDatasetId()));
				study.setTitle(dataset.getDatasetName());
				study.setDescription(dataset.getDatasetDescription());
				study.setPublicReleaseDate(dataset.getCreatedOn());
				File datasetFile = exportTab("s_" + dsId + DateTimeUtils.getFormattedDateTime(new Date()), request, context, Collections.singletonList(dsId));
				study.setFileName(datasetFile.getName());
				resultFiles.add(datasetFile);
				inv.addStudy(study);

				List<ViewTableCollaborators> collaborators = CollaboratorTableResource.getCollaboratorsForDataset(dataset.getDatasetId(), req, resp, userDetails);

				if (!CollectionUtils.isEmpty(collaborators))
					collaborators.forEach(c -> study.addContact(new Person(c.getCollaboratorLastName(), c.getCollaboratorFirstName(), c.getCollaboratorEmail(), c.getInstitutionName(), c.getInstitutionAddress())));

				Protocol protocol = new Protocol("Phenotyping");
				List<ViewTableTraits> traits = TraitTableResource.getForDataset(dataset.getDatasetId());

				if (!CollectionUtils.isEmpty(traits))
					traits.forEach(t -> protocol.addParameter(new ProtocolParameter(t.getTraitName())));

				study.addProtocol(protocol);
			}
			catch (GerminateException e)
			{
				e.printStackTrace();
			}
		}

		File invFile = ResourceUtils.createTempFile("i_" + CollectionUtils.join(datasetIds, "-") + DateTimeUtils.getFormattedDateTime(new Date()), ".txt");
		inv.writeToFile(invFile.getAbsolutePath());
		resultFiles.add(invFile);

		FileUtils.zipUp(zipFile, resultFiles);

		return zipFile;
	}

	private File exportTab(String filename, SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
		throws GerminateException, IOException
	{
		File file = ResourceUtils.createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (CollectionUtils.isEmpty(datasetIds)) {
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			// Run the procedure
			ExportTrialsData procedure = new ExportTrialsData();
			procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

			// Set parameters if present
			if (!CollectionUtils.isEmpty(request.getyGroupIds()))
				procedure.setGroupids(CollectionUtils.join(request.getyGroupIds(), ","));
			if (!CollectionUtils.isEmpty(request.getyIds()))
				procedure.setMarkedids(CollectionUtils.join(request.getyIds(), ","));
			if (!CollectionUtils.isEmpty(request.getxIds()))
				procedure.setPhenotypeids(CollectionUtils.join(request.getxIds(), ","));

			// Execute the procedure
			procedure.execute(context.configuration());

			// Write everything to a file
			bw.write("#input=PHENOTYPE" + ResourceUtils.CRLF);
			ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		return file;
	}

	public enum TrialsExportFormat
	{
		tab,
		isatab
	}
}
