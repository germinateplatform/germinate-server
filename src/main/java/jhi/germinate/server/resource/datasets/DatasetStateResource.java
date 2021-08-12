package jhi.germinate.server.resource.datasets;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.*;

@Path("dataset/{datasetId}/state")
@Secured(UserType.ADMIN)
public class DatasetStateResource extends ContextResource
{
	@PathParam("datasetId")
	private Integer datasetId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchDatasetState(Integer stateId)
		throws SQLException, IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, resp, userDetails, false);

		if (dataset == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return false;
		}
		else
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				return context.update(DATASETS)
							  .set(DATASETS.DATASET_STATE_ID, stateId)
							  .where(DATASETS.ID.eq(dataset.getDatasetId()))
							  .execute() > 0;
			}
		}
	}
}
