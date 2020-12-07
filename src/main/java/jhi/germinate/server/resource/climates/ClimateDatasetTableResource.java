package jhi.germinate.server.resource.climates;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateDatasetTableResource extends DatasetTableResource
{
	private Integer climateId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.climateId = Integer.parseInt(getRequestAttributes().get("climateId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	@Override
	public PaginatedResult<List<ViewTableDatasets>> postJson(UnacceptedLicenseRequest request)
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(CLIMATEDATA).where(CLIMATEDATA.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																   .and(CLIMATEDATA.CLIMATE_ID.eq(climateId))))));
	}
}
