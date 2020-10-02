package jhi.germinate.server.resource.compounds;

import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.DatasetTableResource;

import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class CompoundDatasetTableResource extends DatasetTableResource
{
	private Integer compoundId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.compoundId = Integer.parseInt(getRequestAttributes().get("compoundId").toString());
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
																																   .and(COMPOUNDDATA.COMPOUND_ID.eq(compoundId))))));
	}
}
