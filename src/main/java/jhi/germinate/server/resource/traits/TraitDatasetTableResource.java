package jhi.germinate.server.resource.traits;

import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.DatasetTableResource;

import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class TraitDatasetTableResource extends DatasetTableResource
{
	private Integer traitId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.traitId = Integer.parseInt(getRequestAttributes().get("traitId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	@Override
	public PaginatedResult<List<ViewTableDatasets>> postJson(UnacceptedLicenseRequest request)
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA).where(PHENOTYPEDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																   .and(PHENOTYPEDATA.PHENOTYPE_ID.eq(traitId))))));
	}
}
