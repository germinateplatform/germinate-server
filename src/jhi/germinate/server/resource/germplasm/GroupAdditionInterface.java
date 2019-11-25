package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.*;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import java.sql.*;
import java.util.List;

import jhi.germinate.resource.GroupModification;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.resource.importers.FileUploadHandler;
import jhi.germinate.server.util.*;

/**
 * @author Sebastian Raubach
 */
public interface GroupAdditionInterface
{
	default int addGroupMembersFromFile(Integer groupId, Representation entity, Field<Integer> field, Table table, Request req, Response resp)
	{
		if (groupId == null || entity == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		FileUploadHandler.ReaderResult result = FileUploadHandler.readAllLines(entity, "textfile", "column");

		if (!StringUtils.isEmpty(result.getColumn()) && !CollectionUtils.isEmpty(result.getLines()))
		{
			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn))
			{
				List<Integer> newIds = context.select(field)
											  .from(table)
											  .where(DSL.field(result.getColumn()).in(result.getLines()))
											  .fetchInto(Integer.class);

				if (CollectionUtils.isEmpty(newIds))
					return 0;

				GroupModification modification = new GroupModification();
				modification.setAddition(true);
				modification.setIds(newIds.toArray(new Integer[0]));
				return GroupResource.patchGroupMembers(groupId, req, resp, modification);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}
		}
		else
		{
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
