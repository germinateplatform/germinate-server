package jhi.germinate.server.resource.climates;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.BaseDatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("climate/{climateId}/dataset")
@Secured
@PermitAll
public class ClimateDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postClimateDatasetTable(UnacceptedLicenseRequest request, @PathParam("climateId") Integer climateId)
			throws SQLException
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(CLIMATEDATA).where(CLIMATEDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																															   .and(CLIMATEDATA.CLIMATE_ID.eq(climateId))))));
	}
}
