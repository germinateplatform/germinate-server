package jhi.germinate.server.resource.traits;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Phenotypedata.*;
import static jhi.germinate.server.database.tables.Phenotypes.*;
import static jhi.germinate.server.database.tables.Synonyms.*;
import static jhi.germinate.server.database.tables.Units.*;
import static jhi.germinate.server.database.tables.ViewTableTraits.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTraitResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableTraits> getJson(DatasetRequest request)
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
				PHENOTYPES.ID.as(VIEW_TABLE_TRAITS.TRAIT_ID.getName()),
				PHENOTYPES.NAME.as(VIEW_TABLE_TRAITS.TRAIT_NAME.getName()),
				PHENOTYPES.SHORT_NAME.as(VIEW_TABLE_TRAITS.TRAIT_NAME_SHORT.getName()),
				PHENOTYPES.DESCRIPTION.as(VIEW_TABLE_TRAITS.TRAIT_DESCRIPTION.getName()),
				PHENOTYPES.DATATYPE.as(VIEW_TABLE_TRAITS.DATA_TYPE.getName()),
				UNITS.ID.as(VIEW_TABLE_TRAITS.UNIT_ID.getName()),
				UNITS.UNIT_NAME.as(VIEW_TABLE_TRAITS.UNIT_NAME.getName()),
				UNITS.UNIT_DESCRIPTION.as(VIEW_TABLE_TRAITS.UNIT_DESCRIPTION.getName()),
				UNITS.UNIT_ABBREVIATION.as(VIEW_TABLE_TRAITS.UNIT_ABBREVIATION.getName()),
				SYNONYMS.SYNONYMS_.as(VIEW_TABLE_TRAITS.SYNONYMS.getName()),
				DSL.zero().as(VIEW_TABLE_TRAITS.COUNT.getName())
			)
						  .from(PHENOTYPES.leftJoin(UNITS).on(PHENOTYPES.UNIT_ID.eq(UNITS.ID))
										  .leftJoin(SYNONYMS).on(SYNONYMS.FOREIGN_ID.eq(PHENOTYPES.ID)
																					.and(SYNONYMS.SYNONYMTYPE_ID.eq(4))))
						  .where(DSL.exists(DSL.selectOne().from(PHENOTYPEDATA)
											   .where(PHENOTYPEDATA.DATASET_ID.in(requestedIds))
											   .and(PHENOTYPEDATA.PHENOTYPE_ID.eq(PHENOTYPES.ID))
											   .limit(1)))
						  .orderBy(PHENOTYPES.NAME)
						  .fetchInto(ViewTableTraits.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
