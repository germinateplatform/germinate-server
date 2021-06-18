package jhi.germinate.server.resource.datasets;

import jhi.germinate.resource.ExperimentRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportDatasetAttributes;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.*;

@Path("dataset/attribute/export")
@Secured
@PermitAll
public class DatasetAttributeExportResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postDatasetAttributeExport(ExperimentRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails);

		List<Integer> datasetIds = new ArrayList<>();

		if (request.getExperimentId() != null)
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);
				datasetIds = context.selectDistinct(DATASETS.ID)
									.from(DATASETS)
									.where(DATASETS.EXPERIMENT_ID.eq(request.getExperimentId()))
									.fetchInto(Integer.class);
			}
		}
		else if (!CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		}
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		try
		{
			File file = ResourceUtils.createTempFile("attributes-" + CollectionUtils.join(datasetIds, "-"), ".txt");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				ExportDatasetAttributes procedure = new ExportDatasetAttributes();
				procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));

				procedure.execute(context.configuration());

				ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);
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
						   .type("text/plain")
						   .header("content-disposition", "attachment;filename= \"" + file.getName() + "\"")
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
}
