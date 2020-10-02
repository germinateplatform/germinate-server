package jhi.germinate.server.resource.users;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.DatasetUserModificationRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.records.DatasetpermissionsRecord;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.BaseServerResource;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetUserResource extends BaseServerResource
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
	public boolean patchJson(DatasetUserModificationRequest request)
	{
		if (request == null || this.datasetId == null || !Objects.equals(this.datasetId, request.getDatasetId()) || request.isAddOperation() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			if (request.isAddOperation())
			{
				List<Integer> existingIds = context.selectDistinct(DATASETPERMISSIONS.USER_ID).from(DATASETPERMISSIONS).where(DATASETPERMISSIONS.DATASET_ID.eq(request.getDatasetId())).fetchInto(Integer.class);
				List<Integer> toAdd = new ArrayList<>(Arrays.asList(request.getUserIds()));

				toAdd.removeAll(existingIds);

				InsertValuesStep2<DatasetpermissionsRecord, Integer, Integer> step = context.insertInto(DATASETPERMISSIONS, DATASETPERMISSIONS.USER_ID, DATASETPERMISSIONS.DATASET_ID);

				toAdd.forEach(id -> step.values(id, request.getDatasetId()));

				return step.execute() > 0;
			}
			else
			{
				return context.deleteFrom(DATASETPERMISSIONS)
							  .where(DATASETPERMISSIONS.DATASET_ID.eq(request.getDatasetId()))
							  .and(DATASETPERMISSIONS.USER_ID.in(request.getUserIds()))
							  .execute() > 0;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@MinUserType(UserType.ADMIN)
	@Get("json")
	public List<ViewUserDetails> getJson()
	{
		if (datasetId == null)
		{
			return GatekeeperClient.getUsers();
		}
		else
		{
			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn))
			{
				List<ViewUserDetails> result = context.select(
					DATASETPERMISSIONS.USER_ID.as("id"),
					DSL.val("", String.class).as("username"),
					DSL.val("", String.class).as("full_name"),
					DSL.val("", String.class).as("email_address"),
					DSL.val("", String.class).as("name")
				)
													  .from(DATASETPERMISSIONS)
													  .where(DATASETPERMISSIONS.DATASET_ID.eq(datasetId))
													  .fetchInto(ViewUserDetails.class);

				return result.stream()
							 .map(r -> GatekeeperClient.getUser(r.getId()))
							 .filter(Objects::nonNull)
							 .collect(Collectors.toList());
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}
		}
	}
}
