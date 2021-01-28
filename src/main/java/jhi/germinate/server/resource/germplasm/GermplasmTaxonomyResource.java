package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.TaxonCount;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTaxonomyResource extends ServerResource
{
	@Get("json")
	public TaxonCount getTaxonomies()
	{
		try (DSLContext context = Database.getContext())
		{
			TaxonCount result = new TaxonCount();

			result.setGenus(context.select(TAXONOMIES.GENUS.as("taxonomy"), DSL.count().as("count"))
								   .from(TAXONOMIES)
								   .leftJoin(GERMINATEBASE).on(GERMINATEBASE.TAXONOMY_ID.eq(TAXONOMIES.ID))
								   .groupBy(TAXONOMIES.GENUS)
								   .fetchInto(TaxonCount.LevelCount.class));
			result.setSpecies(context.select(TAXONOMIES.SPECIES.as("taxonomy"), DSL.count().as("count"))
									 .from(TAXONOMIES)
									 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.TAXONOMY_ID.eq(TAXONOMIES.ID))
									 .groupBy(TAXONOMIES.SPECIES)
									 .fetchInto(TaxonCount.LevelCount.class));
			result.setSubtaxa(context.select(TAXONOMIES.SUBTAXA.as("taxonomy"), DSL.count().as("count"))
									 .from(TAXONOMIES)
									 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.TAXONOMY_ID.eq(TAXONOMIES.ID))
									 .groupBy(TAXONOMIES.SUBTAXA)
									 .fetchInto(TaxonCount.LevelCount.class));

			return result;
		}
	}
}
