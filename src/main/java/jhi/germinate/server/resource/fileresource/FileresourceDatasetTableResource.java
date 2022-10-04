package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.BaseDatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("fileresource/{fileresourceId}/dataset")
@Secured
@PermitAll
public class FileresourceDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postFileresourceDatasetTable(PaginatedRequest request, @PathParam("fileresourceId") Integer fileresourceId)
		throws SQLException
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne()
																	.from(DATASETFILERESOURCES)
																	.where(DATASETFILERESOURCES.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID))
																	.and(DATASETFILERESOURCES.FILERESOURCE_ID.eq(fileresourceId)))));
	}
}
