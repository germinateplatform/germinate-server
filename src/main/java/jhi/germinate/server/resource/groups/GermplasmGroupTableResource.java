package jhi.germinate.server.resource.groups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableGroups.*;

@Path("germplasm/{germplasmId}/group")
@Secured
@PermitAll
public class GermplasmGroupTableResource extends BaseResource
{
	@PathParam("germplasmId")
	private Integer germplasmId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGroups>> postGermplasmGroupTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (germplasmId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GROUPS);

			from.where(VIEW_TABLE_GROUPS.GROUP_VISIBILITY.eq(true) // Get groups that are visible
														 .or(VIEW_TABLE_GROUPS.USER_ID.eq(userDetails.getId()))) // Or that the user owns
				.and(VIEW_TABLE_GROUPS.GROUP_TYPE.eq("germinatebase")) // Then only get germplasm groups
				.andExists(DSL.selectOne().from(GROUPMEMBERS) // And check if this germplasm is in the group
							  .where(GROUPMEMBERS.GROUP_ID.eq(VIEW_TABLE_GROUPS.GROUP_ID)
														  .and(GROUPMEMBERS.FOREIGN_ID.eq(germplasmId))));

			// Filter here!
			where(from, filters);

			List<ViewTableGroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
