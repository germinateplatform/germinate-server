package jhi.germinate.server.resource.datasets.export;

import com.google.gson.Gson;
import de.ipk_gatersleben.bit.bi.isa4j.components.*;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
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
import jhi.oddjob.JobInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Climatedata.CLIMATEDATA;
import static jhi.germinate.server.database.codegen.tables.DataExportJobs.DATA_EXPORT_JOBS;
import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.DATASETACCESSLOGS;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;
import static jhi.germinate.server.database.codegen.tables.Treatments.TREATMENTS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.VIEW_TABLE_CLIMATES;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

@Path("dataset/export")
@Secured
@PermitAll
public class DatasetExportResource extends ContextResource
{
	@POST
	@Path("/allelefreq")
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, "allelefreq", request.getDatasetIds(), true);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), req, userDetails, true);

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
			dbJob.setDatatype(DataExportJobsDatatype.allelefreq);
			dbJob.setJobConfig(new ExportJobDetails()
					.setBaseFolder(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
					.setxIds(request.getxIds())
					.setxGroupIds(request.getxGroupIds())
					.setyIds(request.getyIds())
					.setyGroupIds(request.getyGroupIds())
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
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}

	@POST
	@Path("/allelefreq/histogram")
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, "allelefreq", request.getDatasetIds(), true);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), req, userDetails, true);

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
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, "climate", request.getDatasetIds(), true);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				try
				{
					File file = exportTabFastClimate("climate-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);

//					String climateIdString = CollectionUtils.join(request.getxIds(), ",");
//					String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
//					String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");
//
//					ExportClimateData procedure = new ExportClimateData();
//					procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));
//
//					if (!StringUtils.isEmpty(groupIdString))
//						procedure.setGroupids(groupIdString);
//					if (!StringUtils.isEmpty(germplasmIdString))
//						procedure.setMarkedids(germplasmIdString);
//					if (!StringUtils.isEmpty(climateIdString))
//						procedure.setClimateids(climateIdString);
//
//					procedure.execute(context.configuration());
//
//					ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);

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
								   .type(MediaType.TEXT_PLAIN)
								   .header("content-disposition", "attachment; filename=\"" + file.getName() + "\"")
								   .header("content-length", file.length())
								   .build();
				}
				catch (GerminateException e)
				{
					conn.close();
					resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
					return null;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return null;
			}
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
	@NeedsDatasets
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

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, "trials", request.getDatasetIds(), true);

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
						file = exportTabFast("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);
//						file = exportTab("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + DateTimeUtils.getFormattedDateTime(new Date()), request, context, datasetIds);
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
	@NeedsDatasets
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

				List<ViewTableCollaborators> collaborators = DatasetCollaboratorTableResource.getCollaboratorsForDataset(dataset.getDatasetId(), req, resp, userDetails);

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

	private File exportTabFast(String filename, SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
			throws GerminateException, IOException
	{
		File file = ResourceUtils.createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (CollectionUtils.isEmpty(datasetIds))
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			// Get the dataset metadata
			Map<Integer, String> datasets = new HashMap<>();
			context.selectFrom(VIEW_TABLE_DATASETS).where(VIEW_TABLE_DATASETS.DATASET_ID.in(datasetIds)).forEach(ds -> {
				String name = ds.getDatasetName() + "\t" + ds.getVersion() + "\t" + ds.getLicenseName();
				datasets.put(ds.getDatasetId(), name);
			});

			Map<Integer, String> locations = new HashMap<>();
			context.selectFrom(LOCATIONS).forEach(l -> locations.put(l.getId(), l.getSiteName()));

			Map<Integer, GroupsRecord> groups = context.selectFrom(GROUPS).fetchMap(GROUPS.ID);

			// Get the requested traits
			Map<Integer, String> traits = new LinkedHashMap<>();
			SelectConditionStep<ViewTableTraitsRecord> step = context.selectFrom(VIEW_TABLE_TRAITS)
																	 .whereExists(DSL.selectOne().from(PHENOTYPEDATA).leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)).where(PHENOTYPEDATA.PHENOTYPE_ID.eq(VIEW_TABLE_TRAITS.TRAIT_ID)).and(TRIALSETUP.DATASET_ID.in(datasetIds)).limit(1));

			// Limit to requested traits
			if (!CollectionUtils.isEmpty(request.getxIds()))
				step.and(VIEW_TABLE_TRAITS.TRAIT_ID.in(request.getxIds()));

			// Map to their display name
			step.forEach(t -> {
				String name = t.getTraitName();

				if (!StringUtils.isEmpty(t.getUnitAbbreviation()))
					name += " [" + t.getUnitAbbreviation() + "]";

				traits.put(t.getTraitId(), name);
			});

			// Optional conditions for germplasm restrictions
			Condition hasMarkedIds = !CollectionUtils.isEmpty(request.getyIds()) ? GERMINATEBASE.ID.in(request.getyIds()) : null;
			Condition hasGroupIds = !CollectionUtils.isEmpty(request.getyGroupIds()) ? GROUPS.ID.in(request.getyGroupIds()) : null;

			// Germplasm lookup
			Map<Integer, ViewTableTrialGermplasm> germplasm = new LinkedHashMap<>();

			// Select the germplasm
			jhi.germinate.server.database.codegen.tables.Germinatebase g = GERMINATEBASE.as("g");
			List<Field<?>> fields = new ArrayList<>(Arrays.asList(
					GERMINATEBASE.ID.as("germplasmId"),
					GERMINATEBASE.NAME.as("germplasmName"),
					GERMINATEBASE.DISPLAY_NAME.as("germplasmDisplayName"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("germplasmGid"),
					TAXONOMIES.GENUS.as("genus"),
					TAXONOMIES.SPECIES.as("species"),
					TAXONOMIES.SUBTAXA.as("subtaxa"),
					MCPD.PUID.as("puid"),
					g.NAME.as("entityParentName"),
					g.GENERAL_IDENTIFIER.as("entityParentGeneralIdentifier")
			));

			if (hasGroupIds != null)
			{
				fields.add(DSL.select(DSL.jsonArrayAgg(GROUPS.ID))
							  .from(GROUPMEMBERS)
							  .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
							  .where(GROUPS.ID.in(request.getyGroupIds()))
							  .and(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
							  .asField("groupIds"));
			}

			SelectOnConditionStep<?> gStep = context.select(fields)
													.from(GERMINATEBASE)
													.leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
													.leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
													.leftJoin(MCPD).on(MCPD.GERMINATEBASE_ID.eq(GERMINATEBASE.ID));

			// Join more tables if groups are requested
			if (hasGroupIds != null)
				gStep.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
					 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID).and(GROUPS.GROUPTYPE_ID.eq(3)));

			// The overall condition, by default only limiting to the germplasm that has phenotypic data in those datasets
			Condition condition = DSL.exists(DSL.selectOne().from(PHENOTYPEDATA).leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)).where(TRIALSETUP.GERMINATEBASE_ID.eq(GERMINATEBASE.ID)).and(TRIALSETUP.DATASET_ID.in(datasetIds)).limit(1));

			// Add the optional conditions
			if (hasMarkedIds != null && hasGroupIds != null)
				condition = condition.and(hasMarkedIds.or(hasGroupIds));
			else if (hasMarkedIds != null)
				condition = condition.and(hasMarkedIds);
			else if (hasGroupIds != null)
				condition = condition.and(hasGroupIds);

			// Run the query and store germplasm mapping
			gStep.where(condition)
				 .forEach(gp -> {
					 ViewTableTrialGermplasm vgp = gp.into(ViewTableTrialGermplasm.class);
					 germplasm.put(vgp.getGermplasmId(), vgp);
				 });

			// Get all treatments into a map
			Map<Integer, String> treatments = new HashMap<>();
			context.selectFrom(TREATMENTS).forEach(t -> treatments.put(t.getId(), t.getName()));

			// Add header rows
			bw.write("#input=PHENOTYPE" + ResourceUtils.CRLF);
			bw.write("name\tdbId\tpuid\tgeneral_identifier\ttaxonomy\tentity_parent_name\tentity_parent_general_identifier\tdataset_name\tdataset_version\tlicense_name\tyear\tgroups\tlocation\tlatitude\tlongitude\televation\ttreatments_description\trep\tblock\ttrial_row\ttrial_column\t");
			bw.write(String.join("\t", traits.values()));

			// Keep track of the data for each germplasm record (name, rep, row, column, treatment)-tuple
			Map<GermplasmRecord, String[]> dataMap = new TreeMap<>();

			// Get the traits in insertion order
			List<String> traitsOrdered = new ArrayList<>(traits.values());

			final Calendar calendar = Calendar.getInstance();

			// Iterate all phenotypic data based on request
			context.select().from(PHENOTYPEDATA)
				   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
				   .where(TRIALSETUP.DATASET_ID.in(datasetIds))
				   .and(TRIALSETUP.GERMINATEBASE_ID.in(germplasm.keySet()))
				   .and(PHENOTYPEDATA.PHENOTYPE_ID.in(traits.keySet()))
//				   .orderBy(PHENOTYPEDATA.GERMINATEBASE_ID, PHENOTYPEDATA.REP, PHENOTYPEDATA.TRIAL_ROW, PHENOTYPEDATA.TRIAL_COLUMN, PHENOTYPEDATA.TREATMENT_ID)
				   .stream()
				   .forEach(pd -> {
					   ViewTableTrialGermplasm gp = germplasm.get(pd.get(TRIALSETUP.GERMINATEBASE_ID));
					   String rep = pd.get(TRIALSETUP.REP);
					   String block = pd.get(TRIALSETUP.BLOCK);
					   Short trialRow = pd.get(TRIALSETUP.TRIAL_ROW);
					   Short trialColumn = pd.get(TRIALSETUP.TRIAL_COLUMN);
					   String traitHeader = traits.get(pd.get(PHENOTYPEDATA.PHENOTYPE_ID));
					   int traitIndex = traitsOrdered.indexOf(traitHeader);
					   String treatment = treatments.get(pd.get(TRIALSETUP.TREATMENT_ID));
					   Integer year = null;

					   Timestamp timestamp = pd.get(PHENOTYPEDATA.RECORDING_DATE);
					   if (timestamp != null)
					   {
						   calendar.setTime(timestamp);
						   year = calendar.get(Calendar.YEAR);
					   }

					   // Create a record
					   GermplasmRecord record = new GermplasmRecord(gp.getGermplasmId(), gp.getGermplasmDisplayName(), gp.getGermplasmName(), rep, block, year, trialRow, trialColumn, treatment, pd.get(TRIALSETUP.DATASET_ID), pd.get(TRIALSETUP.LOCATION_ID), pd.get(TRIALSETUP.LATITUDE, Double.class), pd.get(TRIALSETUP.LONGITUDE, Double.class), pd.get(TRIALSETUP.ELEVATION, Double.class));
					   String value = pd.get(PHENOTYPEDATA.PHENOTYPE_VALUE);

					   // Get or create the data
					   String[] data = dataMap.computeIfAbsent(record, k -> new String[traits.size()]);
					   // Set the trait value
					   data[traitIndex] = value;
				   });

			Gson gson = new Gson();

			// Once we're done, we can start printing it out
			dataMap.entrySet().stream()
				   .forEach(e -> {
					   GermplasmRecord gp = e.getKey();
					   String[] values = e.getValue();
					   ViewTableTrialGermplasm gpdb = germplasm.get(gp.germplasmId);
					   String puid = StringUtils.orEmpty(gpdb.getGermplasmPuid());
					   String gid = StringUtils.orEmpty(gpdb.getGermplasmGid());
					   String genus = gpdb.getGenus();
					   String species = gpdb.getSpecies();
					   String taxonomy = gpdb.getSubtaxa();
					   String tax = StringUtils.join(" ", genus, species, taxonomy);
					   String rep = StringUtils.orEmpty(gp.rep);
					   String block = StringUtils.orEmpty(gp.block);
					   String year = gp.year == null ? "" : String.valueOf(gp.year);
					   String row = gp.trialRow == null ? "" : String.valueOf(gp.trialRow);
					   String col = gp.trialColumn == null ? "" : String.valueOf(gp.trialColumn);
					   String treatment = StringUtils.orEmpty(gp.treatment);
					   String entityParentName = StringUtils.orEmpty(gpdb.getEntityParentName());
					   String entityParentGid = StringUtils.orEmpty(gpdb.getEntityParentGeneralIdentifier());
					   String dataset = gp.datasetId + "-" + datasets.get(gp.datasetId);
					   String location = gp.locationId == null ? "" : locations.get(gp.locationId);
					   String latitude = gp.latitude == null ? "" : String.valueOf(gp.latitude);
					   String longitude = gp.longitude == null ? "" : String.valueOf(gp.longitude);
					   String elevation = gp.elevation == null ? "" : String.valueOf(gp.elevation);
					   String groupString = "";

					   if (!CollectionUtils.isEmpty(gpdb.getGroupIds()))
					   {
						   List<String> germplasmGroups = gpdb.getGroupIds().stream().map(gr -> {
							   GroupsRecord group = groups.get(gr);
							   return StringUtils.truncate(group.getName(), 10);
						   }).sorted().collect(Collectors.toList());

						   groupString = gson.toJson(germplasmGroups);
					   }

					   String mainIdentifier = StringUtils.isEmpty(gp.germplasmDisplayName) ? gp.germplasmName : gp.germplasmDisplayName;

					   bw.write(ResourceUtils.CRLF);
					   bw.write(String.join("\t", mainIdentifier, String.valueOf(gp.germplasmId), puid, gid, tax, entityParentName, entityParentGid, dataset, year, groupString, location, latitude, longitude, elevation, treatment, rep, block, row, col));

					   // Print trait data
					   traits.values().forEach(traitHeader -> {
						   int index = traitsOrdered.indexOf(traitHeader);
						   String value = StringUtils.orEmpty(values[index]);

						   bw.write("\t" + value);
					   });
				   });
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		return file;
	}

	private File exportTabFastClimate(String filename, SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
			throws GerminateException, IOException
	{
		File file = ResourceUtils.createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (CollectionUtils.isEmpty(datasetIds))
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			// Get the dataset metadata
			Map<Integer, String> datasets = new HashMap<>();
			context.selectFrom(VIEW_TABLE_DATASETS).where(VIEW_TABLE_DATASETS.DATASET_ID.in(datasetIds)).forEach(ds -> {
				String name = ds.getDatasetName() + "\t" + ds.getVersion() + "\t" + ds.getLicenseName();
				datasets.put(ds.getDatasetId(), name);
			});

			// Get the requested traits
			Map<Integer, String> climates = new LinkedHashMap<>();
			SelectConditionStep<ViewTableClimatesRecord> step = context.selectFrom(VIEW_TABLE_CLIMATES)
																	   .whereExists(DSL.selectOne().from(CLIMATEDATA).where(CLIMATEDATA.CLIMATE_ID.eq(VIEW_TABLE_CLIMATES.CLIMATE_ID)).and(CLIMATEDATA.DATASET_ID.in(datasetIds)).limit(1));

			// Limit to requested traits
			if (!CollectionUtils.isEmpty(request.getxIds()))
				step.and(VIEW_TABLE_CLIMATES.CLIMATE_ID.in(request.getxIds()));

			// Map to their display name
			step.forEach(t -> {
				String name = t.getClimateName();

				if (!StringUtils.isEmpty(t.getUnitAbbreviation()))
					name += " [" + t.getUnitAbbreviation() + "]";

				climates.put(t.getClimateId(), name);
			});

			// Location lookup
			Map<Integer, ViewTableLocations> locations = new LinkedHashMap<>();

			// Select the locations
			SelectJoinStep<?> lStep = context.select(VIEW_TABLE_LOCATIONS.fields()).from(VIEW_TABLE_LOCATIONS);

			// Optional conditions for germplasm restrictions
			Condition hasMarkedIds = !CollectionUtils.isEmpty(request.getyIds()) ? VIEW_TABLE_LOCATIONS.LOCATION_ID.in(request.getyIds()) : null;
			Condition hasGroupIds = !CollectionUtils.isEmpty(request.getyGroupIds()) ? GROUPS.ID.in(request.getyGroupIds()) : null;

			// Join more tables if groups are requested
			if (hasGroupIds != null)
				lStep.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
					 .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID).and(GROUPS.GROUPTYPE_ID.eq(1)));

			// The overall condition, by default only limiting to the germplasm that has phenotypic data in those datasets
			Condition condition = DSL.exists(DSL.selectOne().from(CLIMATEDATA).where(CLIMATEDATA.LOCATION_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID)).and(CLIMATEDATA.DATASET_ID.in(datasetIds)).limit(1));

			// Add the optional conditions
			if (hasMarkedIds != null && hasGroupIds != null)
				condition = condition.and(hasMarkedIds.or(hasGroupIds));
			else if (hasMarkedIds != null)
				condition = condition.and(hasMarkedIds);
			else if (hasGroupIds != null)
				condition = condition.and(hasGroupIds);

			// Run the query and store germplasm mapping
			lStep.where(condition)
				 .forEach(gp -> {
					 ViewTableLocations vtl = gp.into(ViewTableLocations.class);
					 locations.put(vtl.getLocationId(), vtl);
				 });

			// Add header rows
			bw.write("name\tdbId\tdataset_name\tdataset_version\tlicense_name\tlatitude\tlongitude\televation\tdate\tyear\t");
			bw.write(String.join("\t", climates.values()));

			// Keep track of the data for each location record
			Map<LocationRecord, Double[]> dataMap = new TreeMap<>();

			// Get the traits in insertion order
			List<String> climatesOrdered = new ArrayList<>(climates.values());

			List<Field<?>> fields = new ArrayList<>(List.of(CLIMATEDATA.fields()));
			fields.add(DSL.date(CLIMATEDATA.RECORDING_DATE).as("formatted_date"));

			// Iterate all phenotypic data based on request
			context.select(fields)
				   .from(CLIMATEDATA)
				   .where(CLIMATEDATA.DATASET_ID.in(datasetIds))
				   .and(CLIMATEDATA.LOCATION_ID.in(locations.keySet()))
				   .and(CLIMATEDATA.CLIMATE_ID.in(climates.keySet()))
				   .stream()
				   .forEach(pd -> {
					   String traitHeader = climates.get(pd.get(CLIMATEDATA.CLIMATE_ID));
					   int traitIndex = climatesOrdered.indexOf(traitHeader);
					   LocationRecord record = new LocationRecord(pd.get(CLIMATEDATA.LOCATION_ID), pd.get(CLIMATEDATA.DATASET_ID), pd.get("formatted_date", Date.class));
					   // Create a record
					   Double value = pd.get(CLIMATEDATA.CLIMATE_VALUE, Double.class);

					   // Get or create the data
					   Double[] data = dataMap.computeIfAbsent(record, k -> new Double[climates.size()]);
					   // Set the trait value
					   data[traitIndex] = value;
				   });

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

			// Once we're done, we can start printing it out
			dataMap.entrySet().stream()
				   .forEach(e -> {
					   LocationRecord rec = e.getKey();
					   ViewTableLocations loc = locations.get(rec.locationId);
					   Double[] values = e.getValue();
					   String dataset = datasets.get(rec.datasetId);
					   String latitude = loc.getLocationLatitude() == null ? "" : String.valueOf(loc.getLocationLatitude());
					   String longitude = loc.getLocationLongitude() == null ? "" : String.valueOf(loc.getLocationLongitude());
					   String elevation = loc.getLocationElevation() == null ? "" : String.valueOf(loc.getLocationElevation());
					   String date = rec.date == null ? "" : dateFormat.format(rec.date);
					   String year = rec.date == null ? "" : yearFormat.format(rec.date);

					   bw.write(ResourceUtils.CRLF);
					   bw.write(String.join("\t", loc.getLocationName(), String.valueOf(loc.getLocationId()), dataset, latitude, longitude, elevation, date, year));

					   // Print trait data
					   climates.values().forEach(traitHeader -> {
						   int index = climatesOrdered.indexOf(traitHeader);
						   Double value = values[index];

						   if (value != null)
							   bw.write("\t" + value);
						   else
							   bw.write("\t");
					   });
				   });
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		return file;
	}

//	private File exportTab(String filename, SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
//		throws GerminateException, IOException
//	{
//		File file = ResourceUtils.createTempFile(filename, ".txt");
//
//		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
//		{
//			if (CollectionUtils.isEmpty(datasetIds))
//			{
//				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
//				return null;
//			}
//
//			// Run the procedure
//			ExportTrialsData procedure = new ExportTrialsData();
//			procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));
//
//			// Set parameters if present
//			if (!CollectionUtils.isEmpty(request.getyGroupIds()))
//				procedure.setGroupids(CollectionUtils.join(request.getyGroupIds(), ","));
//			if (!CollectionUtils.isEmpty(request.getyIds()))
//				procedure.setMarkedids(CollectionUtils.join(request.getyIds(), ","));
//			if (!CollectionUtils.isEmpty(request.getxIds()))
//				procedure.setPhenotypeids(CollectionUtils.join(request.getxIds(), ","));
//
//			// Execute the procedure
//			procedure.execute(context.configuration());
//
//			// Write everything to a file
//			bw.write("#input=PHENOTYPE" + ResourceUtils.CRLF);
//			ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
//		}
//
//		return file;
//	}

	public enum TrialsExportFormat
	{
		tab,
		isatab
	}

	private static class LocationRecord implements Comparable<LocationRecord>
	{
		private Integer locationId;
		private Integer datasetId;
		private Date    date;

		public LocationRecord(Integer locationId, Integer datasetId, Date date)
		{
			this.locationId = locationId;
			this.datasetId = datasetId;
			this.date = date;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			LocationRecord that = (LocationRecord) o;
			return locationId.equals(that.locationId) && datasetId.equals(that.datasetId) && date.equals(that.date);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(locationId, datasetId, date);
		}

		@Override
		public int compareTo(LocationRecord o)
		{
			if (this == o)
				return 0;

			int result = locationId.compareTo(o.locationId);

			if (result == 0)
				result = ObjectUtils.compare(datasetId, o.datasetId);
			if (result == 0)
				result = ObjectUtils.compare(date, o.date);

			return result;
		}
	}

	private static class GermplasmRecord implements Comparable<GermplasmRecord>
	{
		private Integer germplasmId;
		private String  germplasmDisplayName;
		private String  germplasmName;
		private String  rep;
		private String  block;
		private Integer year;
		private Short   trialRow;
		private Short   trialColumn;
		private String  treatment;
		private Integer datasetId;
		private Integer locationId;
		private Double  latitude;
		private Double  longitude;
		private Double  elevation;

		public GermplasmRecord(Integer germplasmId, String germplasmDisplayName, String germplasmName, String rep, String block, Integer year, Short trialRow, Short trialColumn, String treatment, Integer datasetId, Integer locationId, Double latitude, Double longitude, Double elevation)
		{
			this.germplasmId = germplasmId;
			this.germplasmDisplayName = germplasmDisplayName;
			this.germplasmName = germplasmName;
			this.rep = rep;
			this.block = block;
			this.year = year;
			this.trialRow = trialRow;
			this.trialColumn = trialColumn;
			this.treatment = treatment;
			this.datasetId = datasetId;
			this.locationId = locationId;
			this.latitude = latitude;
			this.longitude = longitude;
			this.elevation = elevation;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			GermplasmRecord that = (GermplasmRecord) o;
			return germplasmId.equals(that.germplasmId) && Objects.equals(germplasmDisplayName, that.germplasmDisplayName) && germplasmName.equals(that.germplasmName) && datasetId.equals(that.datasetId) && Objects.equals(locationId, that.locationId) && Objects.equals(rep, that.rep) && Objects.equals(block, that.block) && Objects.equals(year, that.year) && Objects.equals(trialRow, that.trialRow) && Objects.equals(trialColumn, that.trialColumn) && Objects.equals(treatment, that.treatment);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(germplasmId, germplasmDisplayName, germplasmName, rep, block, year, trialRow, trialColumn, treatment, datasetId, locationId);
		}

		@Override
		public int compareTo(GermplasmRecord o)
		{
			if (this == o)
				return 0;

			int result = germplasmId.compareTo(o.germplasmId);

			if (result == 0)
				result = ObjectUtils.compare(datasetId, o.datasetId);
			if (result == 0)
				result = ObjectUtils.compare(locationId, o.locationId);
			if (result == 0)
				result = ObjectUtils.compare(rep, o.rep);
			if (result == 0)
				result = ObjectUtils.compare(block, o.block);
			if (result == 0)
				result = ObjectUtils.compare(year, o.year);
			if (result == 0)
				result = ObjectUtils.compare(trialRow, o.trialRow);
			if (result == 0)
				result = ObjectUtils.compare(trialColumn, o.trialColumn);
			if (result == 0)
				result = ObjectUtils.compare(treatment, o.treatment);

			return result;
		}
	}
}
