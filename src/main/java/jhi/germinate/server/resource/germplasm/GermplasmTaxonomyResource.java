package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.TaxonCount;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

@Path("germplasm/taxonomy")
@Secured
@PermitAll
public class GermplasmTaxonomyResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TaxonCount getTaxonomies()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
