package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Traitcategories;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Traitcategories.TRAITCATEGORIES;

@Path("trait/category")
@Secured
@PermitAll
public class TraitCategoryResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Traitcategories> getTraitCategories()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(TRAITCATEGORIES).fetchInto(Traitcategories.class);
		}
	}
}
