package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.BaseDatasetTableResource;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;

@Path("trait/{traitId}/dataset")
@Secured
@PermitAll
public class TraitDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postTraitDatasetTable(UnacceptedLicenseRequest request, @PathParam("traitId") Integer traitId)
			throws IOException, SQLException
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA).leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)).where(TRIALSETUP.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																																	   .and(PHENOTYPEDATA.PHENOTYPE_ID.eq(traitId))))));
	}
}
