package jhi.germinate.server.resource.datasets.export;

import de.ipk_gatersleben.bit.bi.isa4j.components.Protocol;
import de.ipk_gatersleben.bit.bi.isa4j.components.*;
import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.routines.ExportTrialsData;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.DatasetaccesslogsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.*;
import jhi.germinate.server.resource.traits.TraitTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;

/**
 * @author Sebastian Raubach
 */
public class TrialExportResource extends BaseServerResource
{
	public static final String FORMAT = "format";

	private TrialsExportFormat format = TrialsExportFormat.tab;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.format = TrialsExportFormat.valueOf(getQueryValue(FORMAT));
		}
		catch (Exception e)
		{
			this.format = TrialsExportFormat.tab;
		}
	}

	@Post
	public FileRepresentation postJson(SubsettedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		Logger.getLogger("").info("FORMAT: " + format);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		File file;
		Disposition disposition;
		FileRepresentation representation;

		try (DSLContext context = Database.getContext())
		{
			switch (format)
			{
				case isatab:
					file = exportIsaTab(request, context, datasetIds);
					disposition = new Disposition(Disposition.TYPE_ATTACHMENT);
					disposition.setFilename(file.getName());
					representation = new FileRepresentation(file, MediaType.APPLICATION_ZIP);
					representation.setSize(file.length());
					representation.setDisposition(disposition);
					break;
				case tab:
				default:
					file = exportTab("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + getFormattedDateTime(new Date()), request, context, datasetIds);
					disposition = new Disposition(Disposition.TYPE_ATTACHMENT);
					disposition.setFilename(file.getName());
					representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
					representation.setSize(file.length());
					representation.setDisposition(disposition);
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}

	private File exportIsaTab(SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
		throws IOException
	{
		File zipFile = createTempFile(null, "trials-" + CollectionUtils.join(datasetIds, "-") + "-" + getFormattedDateTime(new Date()), ".zip", false);
		List<File> resultFiles = new ArrayList<>();

		Investigation inv = new Investigation("Germinate");
		inv.setTitle("Germinate");
		inv.setDescription("This dataset contains phenotypic data exported from Germinate");

		for (Integer dsId : datasetIds)
		{
			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(dsId, getRequest(), getResponse(), true);

			if (dataset == null)
				continue;

			Study study = new Study(Integer.toString(dataset.getDatasetId()));
			study.setTitle(dataset.getDatasetName());
			study.setDescription(dataset.getDatasetDescription());
			study.setPublicReleaseDate(dataset.getCreatedOn());
			File datasetFile = exportTab("s_" + dsId + getFormattedDateTime(new Date()), request, context, Collections.singletonList(dsId));
			study.setFileName(datasetFile.getName());
			resultFiles.add(datasetFile);
			inv.addStudy(study);

			List<ViewTableCollaborators> collaborators = CollaboratorTableResource.getCollaboratorsForDataset(dataset.getDatasetId(), getRequest(), getResponse());

			if (!CollectionUtils.isEmpty(collaborators))
				collaborators.forEach(c -> study.addContact(new Person(c.getCollaboratorLastName(), c.getCollaboratorFirstName(), c.getCollaboratorEmail(), c.getInstitutionName(), c.getInstitutionAddress())));

			Protocol protocol = new Protocol("Phenotyping");
			List<ViewTableTraits> traits = TraitTableResource.getForDataset(dataset.getDatasetId());

			if (!CollectionUtils.isEmpty(traits))
				traits.forEach(t -> protocol.addParameter(new ProtocolParameter(t.getTraitName())));

			study.addProtocol(protocol);
		}

		File invFile = createTempFile("i_" + CollectionUtils.join(datasetIds, "-") + getFormattedDateTime(new Date()), ".txt");
		inv.writeToFile(invFile.getAbsolutePath());
		resultFiles.add(invFile);

		FileUtils.zipUp(zipFile, resultFiles);

		return zipFile;
	}

	private File exportTab(String filename, SubsettedDatasetRequest request, DSLContext context, List<Integer> datasetIds)
		throws IOException
	{
		File file = createTempFile(filename, ".txt");

		try (PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
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
			bw.write("#input=PHENOTYPE" + CRLF);
			exportToFile(bw, procedure.getResults().get(0), true, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return file;
	}

	public enum TrialsExportFormat
	{
		tab,
		isatab
	}
}
