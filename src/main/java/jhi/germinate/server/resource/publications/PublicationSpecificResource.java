package jhi.germinate.server.resource.publications;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PublicationdataReferenceType;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePublications;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Publicationdata.*;
import static jhi.germinate.server.database.codegen.tables.Publications.*;

@Path("publicationtype")
@Secured
@PermitAll
public class PublicationSpecificResource extends ContextResource
{
	@GET
	@Path("/{referenceType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTablePublications> getPublicationsForType(@PathParam("referenceType") PublicationdataReferenceType referenceType)
		throws IOException, SQLException
	{
		return this.getPublicationsForTypeAndId(referenceType, null);
	}

	@GET
	@Path("/{referenceType}/{referenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTablePublications> getPublicationsForTypeAndId(@PathParam("referenceType") PublicationdataReferenceType referenceType, @PathParam("referenceId") Integer referenceId)
		throws IOException, SQLException
	{
		if (referenceType == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			SelectOnConditionStep<?> step = context.select(
													   PUBLICATIONS.ID.as("publication_id"),
													   PUBLICATIONS.DOI.as("publication_doi"),
													   PUBLICATIONS.FALLBACK_CACHE.as("publication_fallback_cache"),
													   DSL.inline((Integer[]) null).as("referencing_ids"),
													   PUBLICATIONS.CREATED_ON.as("created_on"),
													   PUBLICATIONS.UPDATED_ON.as("updated_on")
												   ).from(PUBLICATIONS)
												   .leftJoin(PUBLICATIONDATA).on(PUBLICATIONDATA.PUBLICATION_ID.eq(PUBLICATIONS.ID));

			Condition mainCondition = PUBLICATIONDATA.REFERENCE_TYPE.eq(referenceType);

			if (referenceId != null)
				mainCondition = mainCondition.and(PUBLICATIONDATA.FOREIGN_ID.eq(referenceId));

			SelectConditionStep<?> where = step.where(mainCondition);

			if (referenceType == PublicationdataReferenceType.germplasm && referenceId != null)
			{
				AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
				List<Integer> groupIds = GroupResource.getGroupIdsForUser(userDetails, referenceId);

				where = where.or(PUBLICATIONDATA.REFERENCE_TYPE.eq(PublicationdataReferenceType.group).and(PUBLICATIONDATA.FOREIGN_ID.in(groupIds)));
			}

			return where.orderBy(PUBLICATIONS.CREATED_ON.desc()).fetchInto(ViewTablePublications.class);
		}
	}
}
