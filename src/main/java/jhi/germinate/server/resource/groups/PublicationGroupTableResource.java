package jhi.germinate.server.resource.groups;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableGroups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.*;

@Path("publication/{publicationId}/group")
@Secured
@PermitAll
public class PublicationGroupTableResource extends GermplasmBaseResource
{
	@PathParam("publicationId")
	private Integer publicationId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroups>> postPublicationGermplasmTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
											   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
											   .and(VIEW_TABLE_PUBLICATIONS.GROUP_IDS.isNotNull())
											   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			Integer[] ids = pub.getGroupIds();

			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GROUPS);

			from.where(VIEW_TABLE_GROUPS.GROUP_ID.in(ids))
				.and(VIEW_TABLE_GROUPS.GROUP_VISIBILITY.eq(true)
													   .or(VIEW_TABLE_GROUPS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			where(from, filters);

			List<ViewTableGroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroups.class);


			result.forEach(g -> {
				ViewUserDetails user = GatekeeperClient.getUser(g.getUserId());

				if (user != null)
					g.setUserName(user.getFullName());
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
