package jhi.germinate.server.resource.traits;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableClimates;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Climateoverlays.*;
import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Units.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetClimateResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableClimates> getJson(DatasetRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> requestedIds;

		if (CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			requestedIds = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		}
		else
		{
			requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
			requestedIds.retainAll(DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse()));
		}

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (DSLContext context = Database.getContext())
		{
			return context.select(
				CLIMATES.ID.as(VIEW_TABLE_CLIMATES.CLIMATE_ID.getName()),
				CLIMATES.NAME.as(VIEW_TABLE_CLIMATES.CLIMATE_NAME.getName()),
				CLIMATES.SHORT_NAME.as(VIEW_TABLE_CLIMATES.CLIMATE_NAME_SHORT.getName()),
				CLIMATES.DESCRIPTION.as(VIEW_TABLE_CLIMATES.CLIMATE_DESCRIPTION.getName()),
				CLIMATES.DATATYPE.as(VIEW_TABLE_CLIMATES.DATA_TYPE.getName()),
				UNITS.ID.as(VIEW_TABLE_CLIMATES.UNIT_ID.getName()),
				UNITS.UNIT_NAME.as(VIEW_TABLE_CLIMATES.UNIT_NAME.getName()),
				UNITS.UNIT_DESCRIPTION.as(VIEW_TABLE_CLIMATES.UNIT_DESCRIPTION.getName()),
				UNITS.UNIT_ABBREVIATION.as(VIEW_TABLE_CLIMATES.UNIT_ABBREVIATION.getName()),
				DSL.selectCount().from(CLIMATEOVERLAYS).where(CLIMATEOVERLAYS.CLIMATE_ID.eq(CLIMATES.ID)).asField(VIEW_TABLE_CLIMATES.OVERLAYS.getName()),
				DSL.zero().as(VIEW_TABLE_CLIMATES.COUNT.getName())
			)
						  .from(CLIMATES.leftJoin(UNITS).on(CLIMATES.UNIT_ID.eq(UNITS.ID)))
						  .where(DSL.exists(DSL.selectOne().from(CLIMATEDATA)
											   .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
											   .and(CLIMATEDATA.CLIMATE_ID.eq(CLIMATES.ID))
											   .limit(1)))
						  .orderBy(CLIMATES.NAME)
						  .fetchInto(ViewTableClimates.class);
		}
	}
}
