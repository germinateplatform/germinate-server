package jhi.germinate.server.resource.datasets;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import java.sql.SQLException;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.DATASETMEMBERS;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;

@Path("marker/{markerId}/dataset")
@Secured
@PermitAll
public class MarkerDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postMarkerDatasetsTable(UnacceptedLicenseRequest request, @PathParam("markerId") Integer markerId)
			throws SQLException
	{
		return Response.ok(runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
		                                                                                                                                         .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
		                                                                                                                                         .and(DATASETMEMBERS.FOREIGN_ID.eq(markerId))))))).build();
	}
}
