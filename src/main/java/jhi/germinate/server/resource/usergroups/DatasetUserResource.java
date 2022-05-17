package jhi.germinate.server.resource.usergroups;

import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.DatasetUserModificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.records.DatasetpermissionsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.*;

@Path("dataset/{datasetId}/user")
@Secured(UserType.ADMIN)
public class DatasetUserResource extends ContextResource
{
	@PathParam("datasetId")
	private Integer datasetId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchDatasetUser(DatasetUserModificationRequest request)
		throws IOException, SQLException
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
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewUserDetails> getDatasetUser()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
	}
}
