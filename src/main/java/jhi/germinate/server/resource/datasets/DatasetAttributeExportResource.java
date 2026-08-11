package jhi.germinate.server.resource.datasets;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.ExperimentRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportDatasetAttributes;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

@Path("dataset/attribute/export")
@Secured
@PermitAll
public class DatasetAttributeExportResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput postDatasetAttributeExport(ExperimentRequest request, @Context HttpServletResponse response)
			throws IOException, SQLException
	{
		if (request == null)
			throw new BadRequestException();

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
			datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		datasetIds.retainAll(AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true));

		if (datasetIds.isEmpty())
			throw new NotFoundException();

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
				throw new InternalServerErrorException();
			}

			return toStreamingResult(file, MediaType.TEXT_PLAIN, response);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}
}
