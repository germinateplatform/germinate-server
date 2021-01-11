package jhi.germinate.server.resource.stats;

import jhi.germinate.resource.enums.OverviewStats;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Experiments.*;
import static jhi.germinate.server.database.codegen.tables.Fileresources.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;

/**
 * @author Sebastian Raubach
 */
public class OverviewStatsResource extends ServerResource
{
	@Get("json")
	public OverviewStats getJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		try (DSLContext context = Database.getContext())
		{
			OverviewStats stats = context.select(
				DSL.selectCount().from(GERMINATEBASE).asField("germplasm"),
				DSL.selectCount().from(MARKERS).asField("markers"),
				DSL.selectCount().from(MAPS).where(MAPS.VISIBILITY.eq(true)).or(MAPS.USER_ID.eq(userDetails.getId())).asField("maps"),
				DSL.selectCount().from(PHENOTYPES).asField("traits"),
				DSL.selectCount().from(CLIMATES).asField("climates"),
				DSL.selectCount().from(LOCATIONS).asField("locations"),
				DSL.selectCount().from(EXPERIMENTS).asField("experiments"),
				DSL.selectCount().from(GROUPS).where(GROUPS.VISIBILITY.eq(true)).or(GROUPS.CREATED_BY.eq(userDetails.getId())).asField("groups"),
				DSL.selectCount().from(IMAGES).asField("images"),
				DSL.selectCount().from(FILERESOURCES).asField("fileresources")
			).fetchSingleInto(OverviewStats.class);

			// Get the datasets this user has access to (ignore if licenses are accepted or not)
			DatasetTableResource.getDatasetsForUser(getRequest(), getResponse(), false)
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
									}
									// Increase the overall count
									stats.setDatasets(stats.getDatasets() + 1);
								});

			return stats;
		}
	}
}
