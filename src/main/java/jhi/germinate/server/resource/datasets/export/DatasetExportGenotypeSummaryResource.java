package jhi.germinate.server.resource.datasets.export;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Datasettypes.*;
import static jhi.germinate.server.database.codegen.tables.Experiments.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;

@Path("dataset/export/genotype/summary")
@Secured
@PermitAll
public class DatasetExportGenotypeSummaryResource extends BaseResource
{
	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postJson(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}


		List<Integer> datasetIds = AuthorizationFilter.restrictDatasetIds(req, "genotype", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(datasetIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			// Prepare the sub-query to get the germplasm count
			SelectConditionStep<Record1<Integer>> germplasmQuery = DSL.select(DSL.countDistinct(DATASETMEMBERS.FOREIGN_ID))
																	  .from(DATASETMEMBERS)
																	  .where(DATASETMEMBERS.DATASET_ID.eq(DATASETS.ID))
																	  .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2));
			Field<?> germplasm;
			if (CollectionUtils.isEmpty(request.getyIds()) && CollectionUtils.isEmpty(request.getyGroupIds()))
			{
				germplasm = germplasmQuery.asField("data_object_count");
			}
			else
			{
				germplasm = germplasmQuery.and(DATASETMEMBERS.FOREIGN_ID.in(request.getyIds())
																		.orExists(DSL.selectOne()
																					 .from(GROUPMEMBERS)
																					 .where(GROUPMEMBERS.FOREIGN_ID.eq(DATASETMEMBERS.FOREIGN_ID))
																					 .and(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))))
										  .asField("data_object_count");
			}

			// Prepare the sub-query to get the marker count
			SelectConditionStep<Record1<Integer>> markerQuery = DSL.select(DSL.countDistinct(DATASETMEMBERS.FOREIGN_ID))
																   .from(DATASETMEMBERS)
																   .where(DATASETMEMBERS.DATASET_ID.eq(DATASETS.ID))
																   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1));
			Field<?> markers;
			if (CollectionUtils.isEmpty(request.getxIds()) && CollectionUtils.isEmpty(request.getxGroupIds()))
			{
				markers = markerQuery.asField("data_point_count");
			}
			else
			{
				markers = markerQuery.and(DATASETMEMBERS.FOREIGN_ID.in(request.getxIds())
																   .orExists(DSL.selectOne()
																				.from(GROUPMEMBERS)
																				.where(GROUPMEMBERS.FOREIGN_ID.eq(DATASETMEMBERS.FOREIGN_ID))
																				.and(GROUPMEMBERS.GROUP_ID.in(request.getxGroupIds()))))
									 .asField("data_point_count");
			}

			SelectOrderByStep<? extends Record> step = context.select(
				DATASETS.ID.as("dataset_id"),
				DATASETS.NAME.as("dataset_name"),
				DATASETS.DESCRIPTION.as("dataset_description"),
				germplasm,
				markers
			)
															  .from(DATASETS)
															  .leftJoin(EXPERIMENTS).on(EXPERIMENTS.ID.eq(DATASETS.EXPERIMENT_ID))
															  .leftJoin(DATASETTYPES).on(DATASETTYPES.ID.eq(DATASETS.DATASETTYPE_ID))
															  .where(DATASETTYPES.DESCRIPTION.eq("genotype"))
															  .and(DATASETS.IS_EXTERNAL.eq(false))
															  .and(DATASETS.ID.in(datasetIds))
															  .groupBy(DATASETS.ID);

			List<ViewTableDatasets> result = setPaginationAndOrderBy(step)
				.fetchInto(ViewTableDatasets.class);

			return new PaginatedResult<>(result, result.size());
		}
	}
}
