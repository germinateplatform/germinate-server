package jhi.germinate.server.resource.datasets.export;

import de.ipk_gatersleben.bit.bi.isa4j.components.*;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.flapjack.io.binning.MakeHistogram;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.ExportJobDetails;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.*;
import jhi.germinate.server.resource.pedigrees.PedigreeResource;
import jhi.germinate.server.resource.traits.TraitTableResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.async.AllelefreqExporter;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import jhi.oddjob.JobInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.DataExportJobs.DATA_EXPORT_JOBS;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.DATASETACCESSLOGS;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.VIEW_TABLE_CLIMATES;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("dataset/export")
@Secured
@PermitAll
public class DatasetExportResource extends ContextResource
{
	@POST
	@Path("/allelefreq")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AsyncExportResult postAlleleFrequencyExport(AlleleFrequencyDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "allelefreq", request.getDatasetIds(), true);

		if (datasetIds.isEmpty())
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.getFirst(), req, userDetails, true);

			if (ds == null)
				return null;

			String uuid = UUID.randomUUID().toString();

			// Get the target folder for all generated files
			File asyncFolder = ResourceUtils.getFromExternal(uuid, "async");
			asyncFolder.mkdirs();

			Integer[] array = {ds.getDatasetId()};

			// Store the job information in the database
			DataExportJobsRecord dbJob = context.newRecord(DATA_EXPORT_JOBS);
			dbJob.setUuid(uuid);
			dbJob.setJobId("N/A");
			dbJob.setDatasetIds(array);
			dbJob.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			dbJob.setDatatype(DataExportJobsDatatype.allelefreq);
			dbJob.setJobConfig(new ExportJobDetails()
					.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
					.setXIds(request.getMarkerIds())
					.setXGroupIds(request.getMarkerGroupIds())
					.setYIds(request.getGermplasmIds())
					.setYGroupIds(request.getGermplasmGroupIds())
					.setSubsetId(request.getMapId())
					.setBinningConfig(request.getConfig())
					.setFileHeaders(String.join("\n", DatasetExportGenotypeResource.getFlapjackHeaders()) + "\n")
					.setFileTypes(request.getFileTypes()));
			dbJob.setStatus(DataExportJobsStatus.waiting);
			if (userDetails.getId() != -1000)
				dbJob.setUserId(userDetails.getId());
			dbJob.store();

			File libFolder = ResourceUtils.getLibFolder();
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add(libFolder.getAbsolutePath() + File.separator + "*");
			args.add(AllelefreqExporter.class.getCanonicalName());
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_SERVER)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_NAME)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PORT)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME)));
			args.add(StringUtils.orEmptyQuotes(PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD)));
			args.add(Integer.toString(dbJob.getId()));

			JobInfo info = ApplicationListener.SCHEDULER.submit("AlleleFrequencyGenotypeExporter", "java", args, asyncFolder.getAbsolutePath());

			// Store the job information in the database
			dbJob.setJobId(info.getId());
			dbJob.store();

			DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
			access.setDatasetId(ds.getDatasetId());
			access.setUserId(userDetails.getId());
			access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			access.store();

			// Return the result
			AsyncExportResult result = new AsyncExportResult();
			result.setUuid(uuid);
			result.setStatus("waiting");
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	@POST
	@Path("/allelefreq/histogram")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput postAlleleFrequencyHistogram(GenotypeSubsetDatasetRequest request, @Context HttpServletResponse response)
			throws StatusException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "allelefreq", request.getDatasetIds(), true);

		if (datasetIds.isEmpty())
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.getFirst(), req, userDetails, true);

			if (ds == null)
				return null;

			Set<String> germplasmNames = DatasetExportGenotypeResource.getGermplasmNames(context, request);
			Set<String> markerNames = DatasetExportGenotypeResource.getMarkerNameList(context, request);

			// Get the source file
			File source = ResourceUtils.getFromExternal(ds.getSourceFile(), "data", "allelefreq");

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

			return toStreamingResult(histogram, MediaType.TEXT_PLAIN, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	@POST
	@Path("/climate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput postDatasetExportClimate(ClimateExportDatasetRequest request, @Context HttpServletResponse response)
			throws SQLException
	{
		if (request == null)
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "climate", request.getDatasetIds(), true);

		if (datasetIds.isEmpty())
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			try
			{
				File file = exportTabFastClimate("climate-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);

				for (Integer dsId : datasetIds)
				{
					DatasetaccesslogsRecord access = context.newRecord(DATASETACCESSLOGS);
					access.setDatasetId(dsId);
					access.setUserId(userDetails.getId());
					access.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					access.store();
				}

				return toStreamingResult(file, MediaType.TEXT_PLAIN, response);
			}
			catch (StatusException e)
			{
				conn.close();
				throw e;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	@POST
	@Path("/trial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public StreamingOutput postDatasetExportTrial(TrialsExportDatasetRequest request, @QueryParam("format") String formatString, @Context HttpServletResponse response)
			throws StatusException, SQLException
	{
		if (request == null)
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "trials", request.getDatasetIds(), true);

		if (datasetIds.isEmpty())
			throw new NotFoundException();

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
					file = exportIsaTab(userDetails, request, context, datasetIds);
					mediaType = "application/zip";
					break;
				case tab:
				default:
					try
					{
						file = exportTabFast("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);
//						file = exportTab("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);
						mediaType = MediaType.TEXT_PLAIN;
					}
					catch (StatusException e)
					{
						conn.close();
						throw e;
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

			return toStreamingResult(file, mediaType, response);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	@POST
	@Path("/pedigree")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_PLAIN, "application/zip"})
	public File postDatasetExportPedigree(PedigreeRequest request, @Context HttpServletResponse response)
			throws IOException, SQLException, StatusException
	{
		if (request.getIncludeAttributes()) {
			return toFileResult(PedigreeResource.exportFlatFile(request, req, securityContext), "application/zip", response);
		} else {
			return toFileResult(PedigreeResource.exportFlatFile(request, req, securityContext), MediaType.TEXT_PLAIN, response);
		}
	}


	private File exportIsaTab(AuthenticationFilter.UserDetails userDetails, TrialsExportDatasetRequest request, DSLContext context, List<Integer> datasetIds)
			throws IOException, SQLException, StatusException
	{
		File zipFile = ResourceUtils.createTempFile(null, "trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".zip", false);
		List<File> resultFiles = new ArrayList<>();

		Investigation inv = new Investigation("Germinate");
		inv.setTitle("Germinate");
		inv.setDescription("This dataset contains phenotypic data exported from Germinate");

		for (Integer dsId : datasetIds)
		{
				ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(dsId, req, userDetails, true);

				if (dataset == null)
					continue;

				Study study = new Study(Integer.toString(dataset.getDatasetId()));
				study.setTitle(dataset.getDatasetName());
				study.setDescription(dataset.getDatasetDescription());
				study.setPublicReleaseDate(dataset.getCreatedOn());
				File datasetFile = exportTabFast("s_" + dsId + DateTimeUtils.getFormattedDateTime(new Date()), request, context, Collections.singletonList(dsId));
				study.setFileName(datasetFile.getName());
				resultFiles.add(datasetFile);
				inv.addStudy(study);

				List<ViewTableCollaborators> collaborators = DatasetCollaboratorTableResource.getCollaboratorsForDataset(dataset.getDatasetId(), req, userDetails);

				if (!CollectionUtils.isEmpty(collaborators))
					collaborators.forEach(c -> study.addContact(new Person(c.getCollaboratorLastName(), c.getCollaboratorFirstName(), c.getCollaboratorEmail(), c.getInstitutionName(), c.getInstitutionAddress())));

				Protocol protocol = new Protocol("Phenotyping");
				List<ViewTableTraits> traits = TraitTableResource.getForDataset(dataset.getDatasetId());

				if (!CollectionUtils.isEmpty(traits))
					traits.forEach(t -> protocol.addParameter(new ProtocolParameter(t.getTraitName())));

				study.addProtocol(protocol);
		}

		File invFile = ResourceUtils.createTempFile("i_" + CollectionUtils.join(datasetIds, "-") + DateTimeUtils.getFormattedDateTime(new Date()), ".txt");
		inv.writeToFile(invFile.getAbsolutePath());
		resultFiles.add(invFile);

		FileUtils.zipUp(zipFile, resultFiles);

		return zipFile;
	}

	private File exportTabFast(String filename, TrialsExportDatasetRequest request, DSLContext context, List<Integer> datasetIds)
			throws IOException, StatusException
	{
		File file = ResourceUtils.createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (CollectionUtils.isEmpty(datasetIds))
				throw new StatusException(Response.Status.NOT_FOUND.getStatusCode());

			bw.write("#input=PHENOTYPE");
			bw.write(System.lineSeparator());
			PhenotypeMatrixBuilder builder = new PhenotypeMatrixBuilder();
			builder.writeTsv(builder.buildMatrix(context, request), bw);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new StatusException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

		return file;
	}

	private File exportTabFastClimate(String filename, ClimateExportDatasetRequest request, DSLContext context, List<Integer> datasetIds)
			throws StatusException, IOException
	{
		File file = ResourceUtils.createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (CollectionUtils.isEmpty(datasetIds))
				throw new StatusException(Response.Status.NOT_FOUND.getStatusCode());

			ClimateMatrixBuilder builder = new ClimateMatrixBuilder();
			builder.writeTsv(builder.buildMatrix(context, request), bw);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new StatusException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

		return file;
	}

	public enum TrialsExportFormat
	{
		tab,
		isatab
	}
}
