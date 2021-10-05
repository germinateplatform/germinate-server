package jhi.germinate.server.resource.traits;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Phenotypes;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

@Path("trait/{traitId}")
@Secured(UserType.ADMIN)
public class TraitResource extends ContextResource
{
	@PathParam("traitId")
	private Integer traitId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean pathTrait(Phenotypes updatedTrait)
		throws SQLException, IOException
	{
		if (updatedTrait == null || StringUtils.isEmpty(updatedTrait.getName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			PhenotypesRecord trait = context.selectFrom(PHENOTYPES).where(PHENOTYPES.ID.eq(traitId)).fetchAny();

			if (trait == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			trait.setName(updatedTrait.getName());
			trait.setDescription(updatedTrait.getDescription());
			trait.setDatatype(updatedTrait.getDatatype());
			trait.setRestrictions(updatedTrait.getRestrictions());
			trait.setShortName(updatedTrait.getShortName());
			trait.setUnitId(updatedTrait.getUnitId());
			return trait.store() > 0;
		}
	}
}
