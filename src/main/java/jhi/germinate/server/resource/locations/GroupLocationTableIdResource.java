package jhi.germinate.server.resource.locations;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.tables.ViewTableGroupLocations.*;

/**
 * @author Sebastian Raubach
 */
public class GroupLocationTableIdResource extends PaginatedServerResource
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

	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record> from = GroupResource.prepareQuery(getRequest(), getResponse(), context, groupId, VIEW_TABLE_GROUP_LOCATIONS, VIEW_TABLE_GROUP_LOCATIONS.GROUP_ID, this, true);

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch(VIEW_TABLE_GROUP_LOCATIONS.LOCATION_ID);

			return new PaginatedResult<>(result, result.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
