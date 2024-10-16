package jhi.germinate.server.resource.institutions;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.InstitutionUnificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Institutions;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Collaborators.COLLABORATORS;
import static jhi.germinate.server.database.codegen.tables.Germplasminstitutions.GERMPLASMINSTITUTIONS;
import static jhi.germinate.server.database.codegen.tables.Institutions.INSTITUTIONS;

@Path("institution/unify")
@Secured(UserType.DATA_CURATOR)
public class InstitutionUnifierResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postInstitutionUnifier(InstitutionUnificationRequest request)
			throws SQLException, IOException
	{
		if (request == null || request.getPreferredInstitutionId() == null || CollectionUtils.isEmpty(request.getInstitutionIds()))
			return Response.status(Response.Status.BAD_REQUEST).build();

		// Remove the preferred id from the list just in case it was added
		List<Integer> ids = new ArrayList<>(Arrays.asList(request.getInstitutionIds()));
		ids.remove(request.getPreferredInstitutionId());

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the database entries matching the requested ids
			Institutions preferred = context.selectFrom(INSTITUTIONS).where(INSTITUTIONS.ID.eq(request.getPreferredInstitutionId())).fetchAnyInto(Institutions.class);
			Integer preferredId = preferred.getId();
			List<Institutions> others = context.selectFrom(INSTITUTIONS).where(INSTITUTIONS.ID.in(ids)).fetchInto(Institutions.class);
			List<Integer> otherIds = others.stream().map(Institutions::getId).collect(Collectors.toList());

			// If there's no preferred one or the others are empty or the only other one is the preferred one, return
			if (preferredId == null || CollectionUtils.isEmpty(otherIds) || (otherIds.size() == 1 && Objects.equals(otherIds.get(0), preferredId)))
				return Response.status(Response.Status.BAD_REQUEST).build();

			context.update(GERMPLASMINSTITUTIONS).set(GERMPLASMINSTITUTIONS.INSTITUTION_ID, preferredId).where(GERMPLASMINSTITUTIONS.INSTITUTION_ID.in(otherIds)).execute();
			context.update(COLLABORATORS).set(COLLABORATORS.INSTITUTION_ID, preferredId).where(COLLABORATORS.INSTITUTION_ID.in(otherIds)).execute();

			// Delete the old ids
			context.deleteFrom(INSTITUTIONS).where(INSTITUTIONS.ID.in(otherIds)).execute();

			return Response.ok(true).build();
		}
	}
}
