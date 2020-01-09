package jhi.germinate.server.resource.locations;

import org.jooq.Result;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.tables.ViewTableGroupLocations.*;

/**
 * @author Sebastian Raubach
 */
public class GroupLocationTableExportResource extends PaginatedServerResource
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
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record> from = GroupResource.prepareQuery(getRequest(), getResponse(), context, groupId, VIEW_TABLE_GROUP_LOCATIONS, VIEW_TABLE_GROUP_LOCATIONS.GROUP_ID, this, false);

			// Filter here!
			filter(from, filters);

			Result<Record> result = setPaginationAndOrderBy(from)
				.fetch();

			return export(result, "marker-group-table-");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
