package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.groups.GroupResource;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;

/**
 * @author Sebastian Raubach
 */
public class GroupGermplasmTableExportResource extends GermplasmBaseResource
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

			SelectOnConditionStep<?> from = getGermplasmQuery(context, GROUPS.ID.as("group_id"), GROUPS.NAME.as("group_name"))
				.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
				.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(GROUPS.GROUPTYPE_ID.eq(3));
			if (groupId != null)
				from.where(GROUPS.ID.eq(groupId));

			// Filter here!
			filter(from, adjustFilter(filters));

			return export(from.fetch(), "germplasm-group-table-");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
