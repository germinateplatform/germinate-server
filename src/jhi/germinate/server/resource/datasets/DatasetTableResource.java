package jhi.germinate.server.resource.datasets;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.Datasetpermissions.*;
import static jhi.germinate.server.database.tables.Usergroupmembers.*;
import static jhi.germinate.server.database.tables.Usergroups.*;
import static jhi.germinate.server.database.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTableResource extends PaginatedServerResource implements FilteredResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableDatasets>> getJson(PaginatedRequest request)
	{
		// TODO: Handle public mode!
		boolean isPublic = false;

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_DATASETS);

			if (!userDetails.getAdmin())
			{
				// Check if the dataset is public or if the user is part of a group that has access or if the user has access themselves
				from.where(VIEW_TABLE_DATASETS.DATASETSTATE.eq("public"))
					.orExists(context.selectOne().from(DATASETPERMISSIONS)
									 .leftJoin(USERGROUPS).on(USERGROUPS.ID.eq(DATASETPERMISSIONS.GROUP_ID))
									 .leftJoin(USERGROUPMEMBERS).on(USERGROUPMEMBERS.USERGROUP_ID.eq(USERGROUPS.ID))
									 .where(DATASETPERMISSIONS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASETID))
									 .and(USERGROUPMEMBERS.USER_ID.eq(userDetails.getId())
																  .or(DATASETPERMISSIONS.USER_ID.eq(userDetails.getId()))));
			}

			// Filter here!
			filter(from, filters);

			List<ViewTableDatasets> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableDatasets.class);

			if (!isPublic)
			{
				Gson gson = new Gson();
				Type type = new TypeToken<List<Integer>>()
				{
				}.getType();
				result.stream()
					  .filter(d -> d.getAcceptedby() != null)
					  .forEach(d -> {
						  String acceptedBy = d.getAcceptedby();
						  List<Integer> ids = gson.fromJson(acceptedBy, type);

						  if (ids != null)
							  d.setAcceptedby(Integer.toString(userDetails.getId()));
					  });
			}

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