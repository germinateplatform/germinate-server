package jhi.germinate.server.resource.datasets;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.ExperimentRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.routines.ExportDatasetAttributes;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Datasets.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetAttributeExportResource extends BaseServerResource
{
	@Post("json")
	public FileRepresentation getJson(ExperimentRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>();

		if (request.getExperimentId() != null)
		{
			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn))
			{
				datasetIds = context.selectDistinct(DATASETS.ID)
					   .from(DATASETS)
					   .where(DATASETS.EXPERIMENT_ID.eq(request.getExperimentId()))
					   .fetchInto(Integer.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if (!CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		}
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		FileRepresentation representation;
		try
		{
			File file = createTempFile("attributes-" + CollectionUtils.join(datasetIds, "-"), ".txt");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				ExportDatasetAttributes procedure = new ExportDatasetAttributes();
				procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

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
