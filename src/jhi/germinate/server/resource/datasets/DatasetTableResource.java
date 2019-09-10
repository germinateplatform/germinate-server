package jhi.germinate.server.resource.datasets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;

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
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_DATASETS);

			if (!userDetails.isAtLeast(UserType.ADMIN))
			{
				// Check if the dataset is public or if the user is part of a group that has access or if the user has access themselves
				from.where(VIEW_TABLE_DATASETS.DATASET_STATE.eq("public")
															.orExists(context.selectOne().from(DATASETPERMISSIONS)
																			 .leftJoin(USERGROUPS).on(USERGROUPS.ID.eq(DATASETPERMISSIONS.GROUP_ID))
																			 .leftJoin(USERGROUPMEMBERS).on(USERGROUPMEMBERS.USERGROUP_ID.eq(USERGROUPS.ID))
																			 .where(DATASETPERMISSIONS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID))
																			 .and(USERGROUPMEMBERS.USER_ID.eq(userDetails.getId())
																										  .or(DATASETPERMISSIONS.USER_ID.eq(userDetails.getId())))));
			}

			// Filter here!
			filter(from, filters);

			List<ViewTableDatasets> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableDatasets.class);

			if (mode != AuthenticationMode.FULL)
			{
				Gson gson = new Gson();
				Type type = new TypeToken<List<Integer>>()
				{
				}.getType();
				result.stream()
					  .filter(d -> d.getAcceptedBy() != null)
					  .forEach(d -> {
						  String acceptedBy = d.getAcceptedBy();
						  List<Integer> ids = gson.fromJson(acceptedBy, type);

						  if (ids != null)
							  d.setAcceptedBy(Integer.toString(userDetails.getId()));
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