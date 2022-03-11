package jhi.germinate.server.resource.traits;

import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportTraitCategorical;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
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

@Path("dataset/categorical/trial")
@Secured
@PermitAll
public class TraitCategoricalResource extends ContextResource
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

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasetsForUser = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "trials", true);
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
			File file = ResourceUtils.createTempFile("traits-" + CollectionUtils.join(request.getxIds(), "-"), ".tsv");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String traitIdString = CollectionUtils.join(request.getxIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getyIds(), ",");
				String groupIdString = CollectionUtils.join(request.getyGroupIds(), ",");

				if (CollectionUtils.isEmpty(datasetIds))
				{
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return null;
				}
				else
				{
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
