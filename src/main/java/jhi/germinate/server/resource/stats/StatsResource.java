package jhi.germinate.server.resource.stats;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.*;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Compounds.*;
import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.*;
import static jhi.germinate.server.database.codegen.tables.Experiments.*;
import static jhi.germinate.server.database.codegen.tables.Fileresources.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.Publications.*;
import static jhi.germinate.server.database.codegen.tables.ViewStatsBiologicalstatus.*;
import static jhi.germinate.server.database.codegen.tables.ViewStatsCountry.*;
import static jhi.germinate.server.database.codegen.tables.ViewStatsPdci.*;
import static jhi.germinate.server.database.codegen.tables.ViewStatsTaxonomy.*;

@Path("stats")
@Secured
@PermitAll
public class StatsResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/biologicalstatus")
	public Response getBioStatusStats()
		throws IOException, SQLException
	{
		return export("biologicalstatus", VIEW_STATS_BIOLOGICALSTATUS);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/country")
	public Response getCountryStats()
		throws IOException, SQLException
	{
		return export("country", VIEW_STATS_COUNTRY);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entitytype")
	public List<EntityTypeStats> getEntityTypeStats()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.select(
							  ENTITYTYPES.ID.as("entity_type_id"),
							  ENTITYTYPES.NAME.as("entity_type_name"),
							  DSL.selectCount().from(GERMINATEBASE).where(GERMINATEBASE.ENTITYTYPE_ID.eq(ENTITYTYPES.ID)).asField("count"))
						  .from(ENTITYTYPES)
						  .fetchInto(EntityTypeStats.class);
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/overview")
	public OverviewStats getJson()
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			// Get the datasets this user has access to (ignore if licenses are accepted or not)
			List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetsForUser(req, resp, userDetails, null, false);
			List<Integer> datasetIds = datasets.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList());

			DSLContext context = Database.getContext(conn);
			OverviewStats stats = context.select(
				DSL.selectCount().from(GERMINATEBASE).asField("germplasm"),
				DSL.selectCount().from(MARKERS).asField("markers"),
				DSL.selectCount().from(MAPS).where(MAPS.VISIBILITY.eq(true)).or(MAPS.USER_ID.eq(userDetails.getId())).asField("maps"),
				DSL.selectCount().from(PHENOTYPES).asField("traits"),
				DSL.selectCount().from(COMPOUNDS).asField("compounds"),
				DSL.selectCount().from(CLIMATES).asField("climates"),
				DSL.selectCount().from(LOCATIONS).asField("locations"),
				DSL.selectCount().from(EXPERIMENTS).asField("experiments"),
				DSL.selectCount().from(GROUPS).where(GROUPS.VISIBILITY.eq(true)).or(GROUPS.CREATED_BY.eq(userDetails.getId())).asField("groups"),
				DSL.selectCount().from(IMAGES).asField("images"),
				DSL.selectCount().from(FILERESOURCES).where(DSL.notExists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID))))
				   .or(DSL.exists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID).and(DATASETFILERESOURCES.DATASET_ID.in(datasetIds))))).asField("fileresources"),
				DSL.selectCount().from(PUBLICATIONS).asField("publications")
			).fetchSingleInto(OverviewStats.class);

			stats.setDatasets(datasets.size());
			datasets.stream()
					.filter(d -> !d.getIsExternal())
					.forEach(d -> {
						// Increase the specific counts
						switch (d.getDatasetType())
						{
							case "genotype":
								stats.setDatasetsGenotype(stats.getDatasetsGenotype() + 1);
								break;
							case "trials":
								stats.setDatasetsTrials(stats.getDatasetsTrials() + 1);
								break;
							case "allelefreq":
								stats.setDatasetsAllelefreq(stats.getDatasetsAllelefreq() + 1);
								break;
							case "climate":
								stats.setDatasetsClimate(stats.getDatasetsClimate() + 1);
								break;
							case "compound":
								stats.setDatasetsCompound(stats.getDatasetsCompound() + 1);
								break;
							case "pedigree":
								stats.setDatasetsPedigree(stats.getDatasetsPedigree() + 1);
								break;
						}
					});

			return stats;
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/pdci")
	public Response getPdciStats()
		throws IOException, SQLException
	{
		return export("pdci", VIEW_STATS_PDCI);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/taxonomy")
	public Response getTaxonomyStats()
		throws IOException, SQLException
	{
		return export("taxonomy", VIEW_STATS_TAXONOMY);
	}

	protected Response export(String filename, TableImpl<? extends Record> table)
		throws IOException, SQLException
	{
		try
		{
			File file = ResourceUtils.createTempFile(filename, ".tsv");

			try (Connection conn = Database.getConnection();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				Result<? extends Record> result = context.selectFrom(table)
														 .fetch();
				ResourceUtils.exportToFile(bw, result, true, null);
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
