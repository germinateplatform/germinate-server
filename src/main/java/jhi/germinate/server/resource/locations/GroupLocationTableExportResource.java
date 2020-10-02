package jhi.germinate.server.resource.locations;

import org.jooq.Result;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.*;

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
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			SelectSelectStep<Record> select = context.select(VIEW_TABLE_LOCATIONS.fields())
													 .select(GROUPS.NAME.as("group_name"))
													 .select(GROUPS.ID.as("group_id"));

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_LOCATIONS)
												.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(VIEW_TABLE_LOCATIONS.LOCATION_ID))
												.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(1));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

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
