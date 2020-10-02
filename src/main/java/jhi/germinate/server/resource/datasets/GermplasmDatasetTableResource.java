package jhi.germinate.server.resource.datasets;

import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;

import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmDatasetTableResource extends DatasetTableResource
{
	private Integer germplasmId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.germplasmId = Integer.parseInt(getRequestAttributes().get("germplasmId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	@Override
	public PaginatedResult<List<ViewTableDatasets>> postJson(UnacceptedLicenseRequest request)
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(COMPOUNDDATA).where(COMPOUNDDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																 .and(COMPOUNDDATA.GERMINATEBASE_ID.eq(germplasmId))))
														 .orExists(DSL.selectOne().from(PHENOTYPEDATA).where(PHENOTYPEDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																	 .and(PHENOTYPEDATA.GERMINATEBASE_ID.eq(germplasmId))))
														 .orExists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																	   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
																																	   .and(DATASETMEMBERS.FOREIGN_ID.eq(germplasmId))))));
	}
}
