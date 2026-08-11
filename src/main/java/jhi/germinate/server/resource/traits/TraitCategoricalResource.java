package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.TrialsExportDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportTraitCategorical;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

@Path("dataset/categorical/trial")
@Secured
@PermitAll
public class TraitCategoricalResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput postTraitCategories(TrialsExportDatasetRequest request, @Context HttpServletResponse response)
			throws SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getTraitIds()))
			throw new BadRequestException();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);

		try
		{
			File file = ResourceUtils.createTempFile("traits-" + CollectionUtils.join(request.getTraitIds(), "-"), ".tsv");

			try (Connection conn = Database.getConnection();
			     PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String traitIdString = CollectionUtils.join(request.getTraitIds(), ",");
				String germplasmIdString = CollectionUtils.join(request.getGermplasmGroupIds(), ",");
				String groupIdString = CollectionUtils.join(request.getGermplasmGroupIds(), ",");

				if (CollectionUtils.isEmpty(datasetIds))
					throw new NotFoundException();
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

			return toStreamingResult(file, MediaType.TEXT_PLAIN, response);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}
}
