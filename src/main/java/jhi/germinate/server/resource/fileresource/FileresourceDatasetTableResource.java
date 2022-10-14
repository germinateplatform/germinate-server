package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.BaseDatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
import static jhi.germinate.server.database.codegen.tables.Fileresources.*;
import static jhi.germinate.server.database.codegen.tables.Fileresourcetypes.*;
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
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		return runQuery(request, query -> {
			SelectConditionStep<?> step = DSL.selectOne()
											 .from(DATASETFILERESOURCES)
											 .leftJoin(FILERESOURCES).on(FILERESOURCES.ID.eq(DATASETFILERESOURCES.FILERESOURCE_ID))
											 .leftJoin(FILERESOURCETYPES).on(FILERESOURCETYPES.ID.eq(FILERESOURCES.FILERESOURCETYPE_ID))
											 .where(DATASETFILERESOURCES.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID))
											 .and(DATASETFILERESOURCES.FILERESOURCE_ID.eq(fileresourceId));

			if (!userDetails.isAtLeast(UserType.DATA_CURATOR))
				step.and(FILERESOURCETYPES.PUBLIC_VISIBILITY.eq(true));

			query.where(DSL.exists(step));
		});
	}
}
