package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableGroupGermplasm;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.tables.ViewTableGroupGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GroupGermplasmTableResource extends PaginatedServerResource
{
	private Integer groupId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.groupId = Integer.parseInt(getRequestAttributes().get("groupId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Patch("json")
	public int patchJson(GroupModification modification)
	{
		return GroupResource.patchGroupMembers(groupId, getRequest(), getResponse(), modification);
	}

	@Post("json")
	public PaginatedResult<List<ViewTableGroupGermplasm>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record> from = GroupResource.prepareQuery(getRequest(), getResponse(), context, groupId, VIEW_TABLE_GROUP_GERMPLASM, VIEW_TABLE_GROUP_GERMPLASM.GROUP_ID, this, false);

			// Filter here!
			filter(from, filters);

			List<ViewTableGroupGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGroupGermplasm.class);

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
