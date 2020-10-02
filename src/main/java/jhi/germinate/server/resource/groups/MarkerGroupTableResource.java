package jhi.germinate.server.resource.groups;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableGroups.*;

/**
 * @author Sebastian Raubach
 */
public class MarkerGroupTableResource extends PaginatedServerResource
{
	private Integer markerId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.markerId = Integer.parseInt(getRequestAttributes().get("markerId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	public PaginatedResult<List<ViewTableGroups>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (markerId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GROUPS);

			from.where(VIEW_TABLE_GROUPS.GROUP_VISIBILITY.eq(true) // Get groups that are visible
														 .or(VIEW_TABLE_GROUPS.USER_ID.eq(userDetails.getId()))) // Or that the user owns
				.and(VIEW_TABLE_GROUPS.GROUP_TYPE.eq("markers")) // Then only get marker groups
				.andExists(DSL.selectOne().from(GROUPMEMBERS) // And check if this marker is in the group
							  .where(GROUPMEMBERS.GROUP_ID.eq(VIEW_TABLE_GROUPS.GROUP_ID)
														  .and(GROUPMEMBERS.FOREIGN_ID.eq(markerId))));

			// Filter here!
			filter(from, filters);

			List<ViewTableGroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
