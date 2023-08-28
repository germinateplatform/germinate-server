package jhi.germinate.server.resource.usergroups;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableUsergroups;
import jhi.germinate.server.database.codegen.tables.records.DatasetpermissionsRecord;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableUsergroups.*;

@Path("dataset/{datasetId}/usergroup")
@Secured({UserType.ADMIN})
public class DatasetUsergroupTableResource extends BaseResource
{
	@PathParam("datasetId")
	private Integer datasetId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchDatasetUsergroupTable(DatasetGroupModificationRequest request)
		throws SQLException, IOException
	{
		if (request == null || this.datasetId == null || !Objects.equals(this.datasetId, request.getDatasetId()) || request.getAddOperation() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			if (request.getAddOperation())
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

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableUsergroups>> postDatasetUsergroupTable(PaginatedRequest request)
		throws IOException, SQLException
	{
		if (datasetId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_USERGROUPS);

			from.where(DSL.exists(DSL.selectOne().from(DATASETPERMISSIONS)
									 .where(DATASETPERMISSIONS.GROUP_ID.eq(VIEW_TABLE_USERGROUPS.USER_GROUP_ID))
									 .and(DATASETPERMISSIONS.DATASET_ID.eq(datasetId))));

			// Filter here!
			where(from, filters);

			List<ViewTableUsergroups> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableUsergroups.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> getDatasetUsergroupIds(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_USERGROUPS.USER_GROUP_ID)
														   .from(VIEW_TABLE_USERGROUPS);

			from.where(DSL.exists(DSL.selectOne().from(DATASETPERMISSIONS)
									 .where(DATASETPERMISSIONS.GROUP_ID.eq(VIEW_TABLE_USERGROUPS.USER_GROUP_ID))
									 .and(DATASETPERMISSIONS.DATASET_ID.eq(datasetId))));

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
