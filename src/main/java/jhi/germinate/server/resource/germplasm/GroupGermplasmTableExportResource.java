package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.groups.GroupResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.util.*;

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

		try (DSLContext context = Database.getContext())
		{
			GroupResource.checkGroupVisibility(context, CustomVerifier.getFromSession(getRequest(), getResponse()), groupId);

			Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
			Field<Integer> fieldGroupTypeId = DSL.field("grouptype_id", Integer.class);
			List<Join<?>> joins = new ArrayList<>();
			joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
			joins.add(new Join<>(GROUPS, GROUPS.ID, GROUPMEMBERS.GROUP_ID));
			SelectOnConditionStep<?> from = getGermplasmQueryWrapped(context, joins, GROUPS.ID.as("group_id"), GROUPS.NAME.as("group_name"))
				.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
				.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID));

			from.where(fieldGroupTypeId.eq(3));
			if (groupId != null)
				from.where(fieldGroupId.eq(groupId));

			// Filter here!
			filter(from, filters);

			return export(from.fetch(), "germplasm-group-table-");
		}
	}
}
