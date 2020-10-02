package jhi.germinate.server.resource.groups;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.germinate.resource.DatasetGroupRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Grouptypes.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableGroups> getJson(DatasetGroupRequest request)
	{
		if (request == null || StringUtils.isEmpty(request.getDatasetType()) || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());
		List<Integer> requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			Field<Integer> count = DSL.countDistinct(GROUPMEMBERS.FOREIGN_ID).as("count");
			SelectSelectStep<? extends Record> step = context.selectDistinct(
				GROUPS.ID.as("group_id"),
				GROUPS.NAME.as("group_name"),
				GROUPS.DESCRIPTION.as("group_description"),
				GROUPTYPES.ID.as("group_type_id"),
				GROUPTYPES.TARGET_TABLE.as("group_type"),
				GROUPS.CREATED_BY.as("user_id"),
				GROUPS.VISIBILITY.as("group_visibility"),
				GROUPS.CREATED_ON.as("created_on"),
				GROUPS.UPDATED_ON.as("updated_on"),
				count
			);

			SelectConditionStep<? extends Record> resultStep = null;
			switch (request.getDatasetType())
			{
				case "climate":
					resultStep = getClimateGroups(step, requestedIds, userDetails.getId());
					break;
				case "trials":
					resultStep = getTrialsGroups(step, requestedIds, userDetails.getId());
					break;
				case "compound":
					resultStep = getCompoundGroups(step, requestedIds, userDetails.getId());
					break;
				case "genotype":
				case "allelefreq":
					if (Objects.equals(request.getGroupType(), "germinatebase"))
						resultStep = getGenotypeAllelefreqGermplasmGroups(step, requestedIds, userDetails.getId());
					else if (Objects.equals(request.getGroupType(), "markers"))
						resultStep = getGenotypeAllelefreqMarkerGroups(step, requestedIds, userDetails.getId());
					break;
			}

			if (resultStep != null)
			{
				return step.groupBy(GROUPS.ID)
						   .having(count.gt(0))
						   .orderBy(GROUPS.NAME)
						   .fetchInto(ViewTableGroups.class);
			}
			else
			{
				return new ArrayList<>();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private SelectConditionStep<? extends Record> getClimateGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds, Integer userId)
	{
		return step.from(GROUPS)
				   .leftJoin(GROUPTYPES).on(GROUPTYPES.ID.eq(GROUPS.GROUPTYPE_ID))
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(1))
				   .and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userId)))
				   .andExists(DSL.selectOne().from(CLIMATEDATA)
								 .where(CLIMATEDATA.LOCATION_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(CLIMATEDATA.DATASET_ID.in(requestedIds)));
	}

	private SelectConditionStep<? extends Record> getTrialsGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds, Integer userId)
	{
		return step.from(GROUPS)
				   .leftJoin(GROUPTYPES).on(GROUPTYPES.ID.eq(GROUPS.GROUPTYPE_ID))
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3))
				   .and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userId)))
				   .andExists(DSL.selectOne().from(PHENOTYPEDATA)
								 .where(PHENOTYPEDATA.GERMINATEBASE_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(PHENOTYPEDATA.DATASET_ID.in(requestedIds)));
	}

	private SelectConditionStep<? extends Record> getCompoundGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds, Integer userId)
	{
		return step.from(GROUPS)
				   .leftJoin(GROUPTYPES).on(GROUPTYPES.ID.eq(GROUPS.GROUPTYPE_ID))
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3))
				   .and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userId)))
				   .andExists(DSL.selectOne().from(COMPOUNDDATA)
								 .where(COMPOUNDDATA.GERMINATEBASE_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(COMPOUNDDATA.DATASET_ID.in(requestedIds)));
	}

	private SelectConditionStep<? extends Record> getGenotypeAllelefreqMarkerGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds, Integer userId)
	{
		return step.from(GROUPS)
				   .leftJoin(GROUPTYPES).on(GROUPTYPES.ID.eq(GROUPS.GROUPTYPE_ID))
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(2))
				   .and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userId)))
				   .andExists(DSL.selectOne().from(DATASETMEMBERS)
								 .where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
								 .and(DATASETMEMBERS.FOREIGN_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(DATASETMEMBERS.DATASET_ID.in(requestedIds)));
	}

	private SelectConditionStep<? extends Record> getGenotypeAllelefreqGermplasmGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds, Integer userId)
	{
		return step.from(GROUPS)
				   .leftJoin(GROUPTYPES).on(GROUPTYPES.ID.eq(GROUPS.GROUPTYPE_ID))
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3))
				   .and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userId)))
				   .andExists(DSL.selectOne().from(DATASETMEMBERS)
								 .where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
								 .and(DATASETMEMBERS.FOREIGN_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(DATASETMEMBERS.DATASET_ID.in(requestedIds)));
	}
}
