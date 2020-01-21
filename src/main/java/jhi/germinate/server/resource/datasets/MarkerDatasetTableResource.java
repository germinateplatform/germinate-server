package jhi.germinate.server.resource.datasets;

import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.database.tables.pojos.ViewTableDatasets;

import static jhi.germinate.server.database.tables.Compounddata.*;
import static jhi.germinate.server.database.tables.Datasetmembers.*;
import static jhi.germinate.server.database.tables.Phenotypedata.*;
import static jhi.germinate.server.database.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class MarkerDatasetTableResource extends DatasetTableResource
{
	private Integer markerId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.markerId = Integer.parseInt(getRequestAttributes().get("markerId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	@Override
	public PaginatedResult<List<ViewTableDatasets>> getJson(UnacceptedLicenseRequest request)
	{
		return runQuery(request, query -> query.where(DSL.exists(DSL.selectOne().from(DATASETMEMBERS).where(DATASETMEMBERS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID)
																																	   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
																																	   .and(DATASETMEMBERS.FOREIGN_ID.eq(markerId))))));
	}
}
