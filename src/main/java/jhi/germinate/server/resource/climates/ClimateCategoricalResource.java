package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.ClimateExportDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportClimateCategorical;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

@Path("dataset/categorical/climate")
@Secured
@PermitAll
public class ClimateCategoricalResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput postJson(ClimateExportDatasetRequest request, @Context HttpServletResponse response)
			throws SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getClimateIds()))
			throw new BadRequestException();

		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", request.getDatasetIds(), true);

		try
		{
			File file = ResourceUtils.createTempFile("climates-" + CollectionUtils.join(request.getClimateIds(), "-"), ".tsv");

			try (Connection conn = Database.getConnection();
			     PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				String climateIdString = CollectionUtils.join(request.getClimateIds(), ",");
				String locationIdString = CollectionUtils.join(request.getLocationIds(), ",");
				String groupIdString = CollectionUtils.join(request.getLocationGroupIds(), ",");

				if (CollectionUtils.isEmpty(datasetIds))
					throw new NotFoundException();
				else
				{
					ExportClimateCategorical procedure = new ExportClimateCategorical();
					if (!CollectionUtils.isEmpty(datasetIds))
						procedure.setDatasetids(CollectionUtils.join(datasetIds, ","));
					if (!StringUtils.isEmpty(groupIdString))
						procedure.setGroupids(groupIdString);
					if (!StringUtils.isEmpty(locationIdString))
						procedure.setMarkedids(locationIdString);
					if (!StringUtils.isEmpty(climateIdString))
						procedure.setClimateid(climateIdString);

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
