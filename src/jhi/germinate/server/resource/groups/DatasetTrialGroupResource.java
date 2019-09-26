package jhi.germinate.server.resource.groups;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Groupmembers.*;
import static jhi.germinate.server.database.tables.Groups.*;
import static jhi.germinate.server.database.tables.Grouptypes.*;
import static jhi.germinate.server.database.tables.Phenotypedata.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTrialGroupResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableGroups> getJson(DatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets.stream().map(ViewTableDatasets::getDatasetId).collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.select(
				GROUPS.ID.as("group_id"),
				GROUPS.NAME.as("group_name"),
				GROUPS.DESCRIPTION.as("group_description"),
				GROUPTYPES.ID.as("group_type_id"),
				GROUPTYPES.TARGET_TABLE.as("group_type"),
				GROUPS.CREATED_BY.as("user_id"),
				GROUPS.VISIBILITY.as("group_visibility"),
				GROUPS.CREATED_ON.as("created_on"),
				GROUPS.UPDATED_ON.as("updated_on"),
				DSL.count(GROUPMEMBERS.FOREIGN_ID).as("count")
			)
				   .from(GROUPS.leftJoin(GROUPTYPES).on(GROUPS.GROUPTYPE_ID.eq(GROUPTYPES.ID))
							   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID)))
				   .where(GROUPTYPES.TARGET_TABLE.eq("germinatebase"))
				   .and(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA)
									  .where(PHENOTYPEDATA.GERMINATEBASE_ID.eq(GROUPMEMBERS.FOREIGN_ID))
									  .and(PHENOTYPEDATA.DATASET_ID.in(requestedIds))
									  .limit(1)))
				   .groupBy(GROUPS.ID)
				   .orderBy(GROUPS.NAME)
				   .fetchInto(ViewTableGroups.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
