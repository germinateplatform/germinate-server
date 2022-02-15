package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.ViewTablePublicationsReferenceType;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePublications;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.*;

@Path("publication/{publicationId}/germplasm")
@Secured
@PermitAll
public class PublicationGermplasmTableResource extends GermplasmBaseResource
{
	@PathParam("publicationId")
	private Integer publicationId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTablePublicationGermplasm>> postPublicationGermplasmTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
											   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
											   .and(VIEW_TABLE_PUBLICATIONS.REFERENCE_TYPE.eq(ViewTablePublicationsReferenceType.germplasm))
											   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			Integer[] ids = pub.getReferencingIds();

			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, null);
			from.where(DSL.field(GERMPLASM_ID, Integer.class).in(ids));

			// Filter here!
			filter(from, filters);

			List<ViewTablePublicationGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTablePublicationGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
