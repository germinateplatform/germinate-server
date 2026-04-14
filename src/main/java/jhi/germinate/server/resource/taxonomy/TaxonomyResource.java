package jhi.germinate.server.resource.taxonomy;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Taxonomies;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;

@Path("taxonomy")
@Secured
@PermitAll
public class TaxonomyResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTaxonomies(@QueryParam("onlyGenusSpecies") Boolean onlyGenusSpecies)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Field<?>> fields = new ArrayList<>();

			if (onlyGenusSpecies == null || !onlyGenusSpecies)
			{
				fields = List.of(TAXONOMIES.fields());
			}
			else
			{
				fields.add(TAXONOMIES.GENUS);
				fields.add(TAXONOMIES.SPECIES);
			}

			return Response.ok(context.selectDistinct(fields).from(TAXONOMIES)
									  .fetchInto(Taxonomies.class))
						   .build();
		}
	}
}
