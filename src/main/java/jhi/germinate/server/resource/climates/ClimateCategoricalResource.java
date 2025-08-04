package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportClimateCategorical;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

@Path("dataset/categorical/climate")
@Secured
@PermitAll
public class ClimateCategoricalResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postJson(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getxIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);

		try
		{
			File file = ResourceUtils.createTempFile("climates-" + CollectionUtils.join(request.getxIds(), "-"), ".tsv");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String climateIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				if (CollectionUtils.isEmpty(datasetIds))
				{
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return null;
				}
				else
				{
					ExportClimateCategorical procedure = new ExportClimateCategorical();
					if (!CollectionUtils.isEmpty(datasetIds))
						procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));
					if (!StringUtils.isEmpty(groupIdString))
						procedure.setGroupids(groupIdString);
					if (!StringUtils.isEmpty(germplasmIdString))
						procedure.setMarkedids(germplasmIdString);
					if (!StringUtils.isEmpty(climateIdString))
						procedure.setClimateid(climateIdString);

					procedure.execute(context.configuration());

					ResourceUtils.exportToFile(bw, procedure.getResults().get(0), true, null);
				}
			}

			java.nio.file.Path filePath = file.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(filePath, output);
							   Files.deleteIfExists(filePath);
						   })
						   .type(MediaType.TEXT_PLAIN)
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
