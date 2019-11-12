package jhi.germinate.server.resource.compounds;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableCompounds;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Compounddata.*;
import static jhi.germinate.server.database.tables.Compounds.*;
import static jhi.germinate.server.database.tables.Synonyms.*;
import static jhi.germinate.server.database.tables.Units.*;
import static jhi.germinate.server.database.tables.ViewTableCompounds.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetCompoundResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableCompounds> getJson(DatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.select(
				COMPOUNDS.ID.as(VIEW_TABLE_COMPOUNDS.COMPOUND_ID.getName()),
				COMPOUNDS.NAME.as(VIEW_TABLE_COMPOUNDS.COMPOUND_NAME.getName()),
				COMPOUNDS.DESCRIPTION.as(VIEW_TABLE_COMPOUNDS.COMPOUND_DESCRIPTION.getName()),
				UNITS.ID.as(VIEW_TABLE_COMPOUNDS.UNIT_ID.getName()),
				UNITS.UNIT_NAME.as(VIEW_TABLE_COMPOUNDS.UNIT_NAME.getName()),
				UNITS.UNIT_DESCRIPTION.as(VIEW_TABLE_COMPOUNDS.UNIT_DESCRIPTION.getName()),
				UNITS.UNIT_ABBREVIATION.as(VIEW_TABLE_COMPOUNDS.UNIT_ABBREVIATION.getName()),
				SYNONYMS.SYNONYMS_.as(VIEW_TABLE_COMPOUNDS.SYNONYMS.getName()),
				DSL.zero().as(VIEW_TABLE_COMPOUNDS.COUNT.getName())
			)
						  .from(COMPOUNDS.leftJoin(UNITS).on(COMPOUNDS.UNIT_ID.eq(UNITS.ID))
										  .leftJoin(SYNONYMS).on(SYNONYMS.FOREIGN_ID.eq(COMPOUNDS.ID)
																					.and(SYNONYMS.SYNONYMTYPE_ID.eq(3))))
						  .where(DSL.exists(DSL.selectOne().from(COMPOUNDDATA)
											   .where(COMPOUNDDATA.DATASET_ID.in(requestedIds))
											   .and(COMPOUNDDATA.COMPOUND_ID.eq(COMPOUNDS.ID))
											   .limit(1)))
						  .orderBy(COMPOUNDS.NAME)
						  .fetchInto(ViewTableCompounds.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
