package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePublications;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

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
	public Response postPublicationGermplasmTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
											   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
											   .and(VIEW_TABLE_PUBLICATIONS.GERMPLASM_IDS.isNotNull())
											   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			Integer[] ids = pub.getGermplasmIds();

			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, false, null);
			from.having(DSL.field(GERMPLASM_ID, Integer.class).in(ids));

			// Filter here!
			having(from, filters, true);

			List<ViewTablePublicationGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTablePublicationGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return Response.ok(new PaginatedResult<>(result, count)).build();
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postPublicationGermplasmTableIds(PaginatedRequest request)
			throws SQLException
	{
		List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
			                                   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
			                                   .and(VIEW_TABLE_PUBLICATIONS.GERMPLASM_IDS.isNotNull())
			                                   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			SelectJoinStep<Record1<Integer>> from = getGermplasmIdQueryWrapped(context, datasetIds, null);
			Integer[] ids = pub.getGermplasmIds();
			from.having(DSL.field(GERMPLASM_ID, Integer.class).in(ids));

			// Filter here!
			having(from, filters, true);

			List<Integer> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(Integer.class);

			return Response.ok(new PaginatedResult<>(result, result.size())).build();
		}
	}
}
