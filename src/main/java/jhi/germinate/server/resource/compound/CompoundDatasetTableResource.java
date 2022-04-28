package jhi.germinate.server.resource.compound;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.BaseDatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("compound/{compoundId}/dataset")
@Secured
@PermitAll
public class CompoundDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postCompoundDatasetTable(UnacceptedLicenseRequest request, @PathParam("compoundId") Integer compoundId)
		throws SQLException
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(COMPOUNDDATA).where(COMPOUNDDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																 .and(COMPOUNDDATA.COMPOUND_ID.eq(compoundId))))));
	}
}
