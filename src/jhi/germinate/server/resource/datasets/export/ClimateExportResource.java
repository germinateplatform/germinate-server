package jhi.germinate.server.resource.datasets.export;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.routines.*;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateExportResource extends BaseServerResource
{
	@Post
	public FileRepresentation postJson(SubsettedDatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		FileRepresentation representation;
		try
		{
			File file = createTempFile("climate-" + CollectionUtils.join(datasetIds, "-"), ".tsv");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
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

				exportToFile(bw, procedure.getResults().get(0), true, null);
			}
			catch (SQLException | IOException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
