package jhi.germinate.server.resource.datasets.export;

import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.routines.ExportTrialsData;
import jhi.germinate.server.database.codegen.tables.records.DatasetaccesslogsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetaccesslogs.*;

/**
 * @author Sebastian Raubach
 */
public class TrialExportResource extends BaseServerResource
{
	@Post
	public FileRepresentation postJson(SubsettedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		FileRepresentation representation;
		try
		{
			File file = createTempFile("trials-" + CollectionUtils.join(datasetIds, "-") + "-" + getFormattedDateTime(new Date()), ".tsv");

			try (DSLContext context = Database.getContext();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				String traitIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				ExportTrialsData procedure = new ExportTrialsData();
				procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);
				if (!StringUtils.isEmpty(germplasmIdString))
					procedure.setMarkedids(germplasmIdString);
				if (!StringUtils.isEmpty(traitIdString))
					procedure.setPhenotypeids(traitIdString);

				procedure.execute(context.configuration());

				bw.write("#input=PHENOTYPE" + CRLF);
				exportToFile(bw, procedure.getResults().get(0), true, null);

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

			Disposition disposition = new Disposition(Disposition.TYPE_ATTACHMENT);
			disposition.setFilename(file.getName());
			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(disposition);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
