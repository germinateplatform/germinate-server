package jhi.germinate.server.resource.datasets;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.util.Secured;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("germplasm/{germplasmId}/dataset")
@Secured
@PermitAll
public class GermplasmDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postGermplasmDatasetTable(UnacceptedLicenseRequest request, @PathParam("germplasmId") Integer germplasmId)
		throws SQLException
	{
		return runQuery(request, query -> query.where(
			DSL.exists(DSL.selectOne().from(PEDIGREES).where(PEDIGREES.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																				 .and(PEDIGREES.GERMINATEBASE_ID.eq(germplasmId))))
			   .orExists(DSL.selectOne().from(PEDIGREEDEFINITIONS).where(PEDIGREEDEFINITIONS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																									   .and(PEDIGREEDEFINITIONS.GERMINATEBASE_ID.eq(germplasmId))))
			   .orExists(DSL.selectOne().from(PHENOTYPEDATA).leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID)).where(TRIALSETUP.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																						   .and(TRIALSETUP.GERMINATEBASE_ID.eq(germplasmId))))
			   .orExists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																							 .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
																							 .and(DATASETMEMBERS.FOREIGN_ID.eq(germplasmId))))));
	}
}
