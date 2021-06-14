package jhi.germinate.server.resource.datasets;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("marker/{markerId}/dataset")
@Secured
@PermitAll
public class MarkerDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postJson(UnacceptedLicenseRequest request, @PathParam("markerId") Integer markerId)
		throws SQLException
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																	 .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
																																	 .and(DATASETMEMBERS.FOREIGN_ID.eq(markerId))))));
	}
}
