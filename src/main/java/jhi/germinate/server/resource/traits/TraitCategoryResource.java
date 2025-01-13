package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Phenotypecategories;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Phenotypecategories.PHENOTYPECATEGORIES;

@Path("trait/category")
@Secured
@PermitAll
public class TraitCategoryResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTraitCategories()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.selectFrom(PHENOTYPECATEGORIES).fetchInto(Phenotypecategories.class)).build();
		}
	}
}
