package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.jooq.Result;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableGroupGermplasm;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.tables.ViewTableGroupGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GroupGermplasmTableExportResource extends PaginatedServerResource
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
			SelectJoinStep<Record> from = GroupResource.prepareQuery(getRequest(), getResponse(), context, groupId, VIEW_TABLE_GROUP_GERMPLASM, VIEW_TABLE_GROUP_GERMPLASM.GROUP_ID, this, false);

			// Filter here!
			filter(from, filters);

			Result<Record> result = setPaginationAndOrderBy(from)
				.fetch();

			return export(result, "germplasm-group-table-");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
