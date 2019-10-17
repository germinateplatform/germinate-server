package jhi.germinate.server.resource.groups;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.germinate.resource.DatasetGroupRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Compounddata.*;
import static jhi.germinate.server.database.tables.Datasetmembers.*;
import static jhi.germinate.server.database.tables.Groupmembers.*;
import static jhi.germinate.server.database.tables.Groups.*;
import static jhi.germinate.server.database.tables.Grouptypes.*;
import static jhi.germinate.server.database.tables.Phenotypedata.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetGroupResource extends BaseServerResource implements FilteredResource
{
	@Post("json")
	public List<ViewTableGroups> getJson(DatasetGroupRequest request)
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
				DSL.count(GROUPMEMBERS.FOREIGN_ID).as("count")
			);

			SelectConditionStep<? extends Record> resultStep = null;
			switch (request.getExperimentType())
			{
				case "trials":
					resultStep = getTrialsGroups(step, requestedIds);
					break;
				case "compound":
					resultStep = getCompoundGroups(step, requestedIds);
					break;
				case "genotype":
				case "allelefreq":
					if (Objects.equals(request.getGroupType(), "germinatebase"))
					{
						resultStep = getGenotypeAllelefreqGermplasmGroups(step, requestedIds);
					}
					else if (Objects.equals(request.getGroupType(), "markers"))
					{
						resultStep = getGenotypeAllelefreqMarkerGroups(step, requestedIds);
					}
					break;
			}

			if (resultStep != null)
			{
				return resultStep.groupBy(GROUPS.ID, GROUPTYPES.ID)
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

	private SelectConditionStep<? extends Record> getTrialsGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds)
	{
		return step.from(PHENOTYPEDATA)
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(PHENOTYPEDATA.GERMINATEBASE_ID))
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .leftJoin(GROUPTYPES).on(GROUPS.GROUPTYPE_ID.eq(GROUPTYPES.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3))
				   .and(PHENOTYPEDATA.DATASET_ID.in(requestedIds));
	}

	private SelectConditionStep<? extends Record> getCompoundGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds)
	{
		return step.from(COMPOUNDDATA)
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(COMPOUNDDATA.GERMINATEBASE_ID))
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .leftJoin(GROUPTYPES).on(GROUPS.GROUPTYPE_ID.eq(GROUPTYPES.ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3))
				   .and(COMPOUNDDATA.DATASET_ID.in(requestedIds));
	}

	private SelectConditionStep<? extends Record> getGenotypeAllelefreqMarkerGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds)
	{
		return step.from(DATASETMEMBERS)
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(DATASETMEMBERS.FOREIGN_ID))
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .leftJoin(GROUPTYPES).on(GROUPS.GROUPTYPE_ID.eq(GROUPTYPES.ID))
				   .where(DATASETMEMBERS.DATASET_ID.in(requestedIds))
				   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(1))
				   .and(GROUPS.GROUPTYPE_ID.eq(2));
	}

	private SelectConditionStep<? extends Record> getGenotypeAllelefreqGermplasmGroups(SelectSelectStep<? extends Record> step, List<Integer> requestedIds)
	{
		return step.from(DATASETMEMBERS)
				   .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(DATASETMEMBERS.FOREIGN_ID))
				   .leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .leftJoin(GROUPTYPES).on(GROUPS.GROUPTYPE_ID.eq(GROUPTYPES.ID))
				   .where(DATASETMEMBERS.DATASET_ID.in(requestedIds))
				   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
				   .and(GROUPS.GROUPTYPE_ID.eq(3));
	}
}
