package jhi.germinate.server.resource.usergroups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableUsergroups;
import jhi.germinate.server.database.codegen.tables.records.DatasetpermissionsRecord;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableUsergroups.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetUsergroupTableResource extends PaginatedServerResource
{
	private Integer datasetId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.datasetId = Integer.parseInt(getRequestAttributes().get("datasetId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@MinUserType(UserType.ADMIN)
	@Patch("json")
	public boolean patchJson(DatasetGroupModificationRequest request)
	{
		if (request == null || this.datasetId == null || !Objects.equals(this.datasetId, request.getDatasetId()) || request.isAddOperation() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			if (request.isAddOperation())
			{
				List<Integer> existingIds = context.selectDistinct(DATASETPERMISSIONS.USER_ID).from(DATASETPERMISSIONS).where(DATASETPERMISSIONS.DATASET_ID.eq(request.getDatasetId())).fetchInto(Integer.class);
				List<Integer> toAdd = new ArrayList<>(Arrays.asList(request.getGroupIds()));

				toAdd.removeAll(existingIds);

				InsertValuesStep2<DatasetpermissionsRecord, Integer, Integer> step = context.insertInto(DATASETPERMISSIONS, DATASETPERMISSIONS.GROUP_ID, DATASETPERMISSIONS.DATASET_ID);

				toAdd.forEach(id -> step.values(id, request.getDatasetId()));

				return step.execute() > 0;
			}
			else
			{
				return context.deleteFrom(DATASETPERMISSIONS)
							  .where(DATASETPERMISSIONS.DATASET_ID.eq(request.getDatasetId()))
							  .and(DATASETPERMISSIONS.GROUP_ID.in(request.getGroupIds()))
							  .execute() > 0;
			}
		}
	}

	@MinUserType(UserType.ADMIN)
	@Post("json")
	public PaginatedResult<List<ViewTableUsergroups>> getJson(PaginatedRequest request)
	{
		if (datasetId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_USERGROUPS);

			from.where(DSL.exists(DSL.selectOne().from(DATASETPERMISSIONS)
									 .where(DATASETPERMISSIONS.GROUP_ID.eq(VIEW_TABLE_USERGROUPS.USER_GROUP_ID))
									 .and(DATASETPERMISSIONS.DATASET_ID.eq(datasetId))));

			// Filter here!
			filter(from, filters);

			List<ViewTableUsergroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableUsergroups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
