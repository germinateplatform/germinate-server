package jhi.germinate.server.resource.traits;

import jhi.germinate.server.util.*;
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
import jhi.germinate.server.database.codegen.routines.ExportTraitCategorical;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;

/**
 * @author Sebastian Raubach
 */
public class TraitCategoricalResource extends BaseServerResource
{
	@Post
	public FileRepresentation postJson(SubsettedDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getxIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> datasetsForUser = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse(), true);
		List<Integer> datasetIds = new ArrayList<>();
		// If datasets were requested, add these to list
		if (!CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			datasetIds.addAll(Arrays.asList(request.getDatasetIds()));
			// Then restrict to the ones that are available
			datasetIds.retainAll(datasetsForUser);
		}
		else
		{
			// Else, use all available ones
			datasetIds = datasetsForUser;
		}

		try
		{
			File file = createTempFile("traits-" + CollectionUtils.join(request.getxIds(), "-"), ".tsv");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				String traitIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				ExportTraitCategorical procedure = new ExportTraitCategorical();
				if (!CollectionUtils.isEmpty(datasetIds))
					procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));
				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);
				if (!StringUtils.isEmpty(germplasmIdString))
					procedure.setMarkedids(germplasmIdString);
				if (!StringUtils.isEmpty(traitIdString))
					procedure.setTraitids(traitIdString);

				procedure.execute(context.configuration());

				exportToFile(bw, procedure.getResults().get(0), true, null);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			FileRepresentation representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));

			return representation;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
