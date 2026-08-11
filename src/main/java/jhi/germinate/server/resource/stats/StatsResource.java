package jhi.germinate.server.resource.stats;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.jooq.GDSL;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.*;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.BIOLOGICALSTATUS;
import static jhi.germinate.server.database.codegen.tables.Climates.CLIMATES;
import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.DATASETFILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.ENTITYTYPES;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Fileresources.FILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.FILERESOURCETYPES;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.PEDIGREES;
import static jhi.germinate.server.database.codegen.tables.Projectgroups.PROJECTGROUPS;
import static jhi.germinate.server.database.codegen.tables.Projectpublications.PROJECTPUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Projects.PROJECTS;
import static jhi.germinate.server.database.codegen.tables.Publications.PUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Stories.STORIES;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;
import static jhi.germinate.server.database.codegen.tables.ViewStatsBiologicalstatus.VIEW_STATS_BIOLOGICALSTATUS;
import static jhi.germinate.server.database.codegen.tables.ViewStatsCountry.VIEW_STATS_COUNTRY;
import static jhi.germinate.server.database.codegen.tables.ViewStatsTaxonomy.VIEW_STATS_TAXONOMY;
import static jhi.germinate.server.database.codegen.tables.ViewTableTaxonomies.VIEW_TABLE_TAXONOMIES;

@Path("stats")
@Secured
@PermitAll
public class StatsResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/biologicalstatus")
	public StreamingOutput getBioStatusStats(@Context HttpServletResponse response)
			throws IOException, SQLException
	{
		File result = export("biologicalstatus", VIEW_STATS_BIOLOGICALSTATUS);
		return toStreamingResult(result, MediaType.TEXT_PLAIN, response);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/country")
	public StreamingOutput getCountryStats(@Context HttpServletResponse response)
			throws IOException, SQLException
	{
		File result = export("country", VIEW_STATS_COUNTRY);
		return toStreamingResult(result, MediaType.TEXT_PLAIN, response);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entitytype")
	public List<EntityTypeStats> getEntityTypeStats()
			throws SQLException
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
	public OverviewStats getJson(@QueryParam("projectIds") List<Integer> projectIds)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			// Get the datasets this user has access to (ignore if licenses are accepted or not)
			List<ViewTableDatasets> datasets = AuthorizationFilter.getDatasets(req, userDetails, null, false);

			if (!CollectionUtils.isEmpty(projectIds))
				datasets = datasets.stream().filter(ds -> projectIds.contains(ds.getProjectId())).toList();

			List<Integer> datasetIds = datasets.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList());

			SelectConditionStep<Record1<Integer>> step = DSL.selectCount()
			                                                .from(FILERESOURCES)
			                                                .leftJoin(FILERESOURCETYPES).on(FILERESOURCETYPES.ID.eq(FILERESOURCES.FILERESOURCETYPE_ID))
			                                                .where(DSL.notExists(DSL.selectOne().from(DATASETFILERESOURCES)
			                                                                        .where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID)))
			                                                          .orExists(DSL.selectOne().from(DATASETFILERESOURCES)
			                                                                       .where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID).and(DATASETFILERESOURCES.DATASET_ID.in(datasetIds)))));

			DSLContext context = Database.getContext(conn);
			OverviewStats stats;

			if (CollectionUtils.isEmpty(projectIds))
			{
				stats = context.select(
						DSL.selectCount().from(GERMINATEBASE).asField("germplasm"),
						DSL.selectCount().from(MARKERS).asField("markers"),
						DSL.selectCount().from(MAPS).where(MAPS.VISIBILITY.eq(true)).or(MAPS.USER_ID.eq(userDetails.getId())).asField("maps"),
						DSL.selectCount().from(VARIABLES).asField("traits"),
						DSL.selectCount().from(CLIMATES).asField("climates"),
						DSL.selectCount().from(LOCATIONS).asField("locations"),
						DSL.selectCount().from(PEDIGREEDEFINITIONS).where(PEDIGREEDEFINITIONS.DATASET_ID.in(datasetIds)).asField()
						   .plus(DSL.selectCount().from(PEDIGREES).where(PEDIGREES.DATASET_ID.in(datasetIds)).asField()).as("pedigreeDefinitions"),
						DSL.selectCount().from(EXPERIMENTS).asField("experiments"),
						DSL.selectCount().from(GROUPS).where(GROUPS.VISIBILITY.eq(true)).or(GROUPS.CREATED_BY.eq(userDetails.getId())).asField("groups"),
						DSL.selectCount().from(IMAGES).asField("images"),
						step.asField("fileresources"),
						DSL.selectCount().from(PUBLICATIONS).asField("publications"),
						DSL.selectCount().from(STORIES).where(STORIES.VISIBILITY.eq(true)).or(STORIES.USER_ID.eq(userDetails.getId())).asField("dataStories"),
						DSL.selectCount().from(PROJECTS).asField("projects"),
						DSL.selectCount().from(VIEW_TABLE_TAXONOMIES).asField("taxonomies")
				).fetchSingleInto(OverviewStats.class);
			}
			else
			{
				stats = context.select(
						DSL.selectCount().from(GERMINATEBASE).asField("germplasm"),
						DSL.selectCount().from(MARKERS).asField("markers"),
						DSL.selectCount().from(MAPS).where(MAPS.VISIBILITY.eq(true)).or(MAPS.USER_ID.eq(userDetails.getId())).asField("maps"),
						DSL.selectCount().from(VARIABLES).asField("traits"),
						DSL.selectCount().from(CLIMATES).asField("climates"),
						DSL.selectCount().from(LOCATIONS).asField("locations"),
						DSL.selectCount().from(PEDIGREEDEFINITIONS).where(PEDIGREEDEFINITIONS.DATASET_ID.in(datasetIds)).asField()
						   .plus(DSL.selectCount().from(PEDIGREES).where(PEDIGREES.DATASET_ID.in(datasetIds)).asField()).as("pedigreeDefinitions"),
						DSL.selectCount().from(EXPERIMENTS).where(EXPERIMENTS.PROJECT_ID.in(projectIds)).asField("experiments"),
						DSL.selectCount().from(GROUPS).leftJoin(PROJECTGROUPS).on(PROJECTGROUPS.GROUP_ID.eq(GROUPS.ID)).where(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId()))).and(PROJECTGROUPS.PROJECT_ID.in(projectIds)).asField("groups"),
						DSL.selectCount().from(IMAGES).asField("images"),
						step.and(FILERESOURCES.PROJECT_ID.in(projectIds)).asField("fileresources"),
						DSL.selectCount().from(PUBLICATIONS).leftJoin(PROJECTPUBLICATIONS).on(PROJECTPUBLICATIONS.PUBLICATION_ID.eq(PUBLICATIONS.ID)).where(PROJECTPUBLICATIONS.PROJECT_ID.in(projectIds)).asField("publications"),
						DSL.selectCount().from(STORIES).where(STORIES.VISIBILITY.eq(true).or(STORIES.USER_ID.eq(userDetails.getId()))).and(STORIES.PROJECT_ID.in(projectIds)).asField("dataStories"),
						DSL.selectCount().from(PROJECTS).asField("projects"),
						DSL.selectCount().from(VIEW_TABLE_TAXONOMIES).asField("taxonomies")
				).fetchSingleInto(OverviewStats.class);
			}

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
	public StreamingOutput getPdciStats(@Context HttpServletResponse response)
			throws IOException, SQLException
	{
		File file = ResourceUtils.createTempFile("pdci", ".tsv");

		try (Connection conn = Database.getConnection();
		     PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			DSLContext context = Database.getContext(conn);

			Map<String, Map<Integer, Integer>> mapping = new TreeMap<>(Collections.reverseOrder());

			context.select(GERMINATEBASE.PDCI, TAXONOMIES.GENUS)
			       .from(GERMINATEBASE)
			       .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
			       .where(GERMINATEBASE.ENTITYTYPE_ID.eq(1))
			       .and(GERMINATEBASE.PDCI.isNotNull())
			       .forEach(r -> {
					   String genus = r.get(TAXONOMIES.GENUS);
					   if (genus == null)
						   genus = "";
					   int pdciLower = (int) Math.floor(r.get(GERMINATEBASE.PDCI));

					   if (!mapping.containsKey(genus))
					   {
						   Map<Integer, Integer> genusMap = new LinkedHashMap<>();
						   for (int i = 0; i < 10; i++)
							   genusMap.put(i, 0);

						   mapping.put(genus, genusMap);
					   }

					   mapping.get(genus).put(pdciLower, mapping.get(genus).get(pdciLower) + 1);
				   });

			bw.write("bin\tgenus\tcount" + ResourceUtils.CRLF);

			mapping.forEach((genus, counts) -> {
				counts.forEach((bin, count) -> {
					bw.write(bin + "-" + (bin + 1) + "\t" + genus + "\t" + count + ResourceUtils.CRLF);
				});
			});
		}

		return toStreamingResult(file, MediaType.TEXT_PLAIN, response);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/taxonomy")
	public StreamingOutput getTaxonomyStats(@Context HttpServletResponse response)
			throws IOException, SQLException
	{
		File result = export("taxonomy", VIEW_STATS_TAXONOMY);
		return toStreamingResult(result, MediaType.TEXT_PLAIN, response);
	}

	protected File export(String filename, TableImpl<? extends Record> table)
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
				throw new InternalServerErrorException();
			}

			return file;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/meta")
	public List<GermplasmMetaStats> getMetaTats()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Field<Double> pdciField = DSL.floor(GERMINATEBASE.PDCI).as("pdci");

			return context.select(
								  TAXONOMIES.GENUS,
								  TAXONOMIES.SPECIES,
								  GDSL.concatWS(" ", TAXONOMIES.GENUS, TAXONOMIES.SPECIES).as("taxonomy"),
								  DSL.substringIndex(BIOLOGICALSTATUS.SAMPSTAT, "(", 1).as("sampstat"),
								  pdciField
						  )
			              .from(GERMINATEBASE)
			              .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
			              .leftJoin(MCPD).on(MCPD.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
			              .leftJoin(BIOLOGICALSTATUS).on(BIOLOGICALSTATUS.ID.eq(MCPD.SAMPSTAT))
			              .where(GERMINATEBASE.ENTITYTYPE_ID.eq(1))
			              .andNot(
								  TAXONOMIES.GENUS.isNull()
					                              .and(pdciField.isNull())
					                              .and(BIOLOGICALSTATUS.SAMPSTAT.isNull())
						  )
			              .fetchInto(GermplasmMetaStats.class);
		}
	}
}
