package jhi.germinate.server.resource.groups;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.DatasetGroupRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableGroups;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Grouptypes.*;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("dataset/group")
@Secured
@PermitAll
public class DatasetGroupResource extends ContextResource
{
	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableGroups> postDatasetGroups(DatasetGroupRequest request)
		throws IOException, SQLException
	{
		if (request == null || StringUtils.isEmpty(request.getDatasetType()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, null, request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
				   .andExists(DSL.selectOne().from(TRIALSETUP)
								 .where(TRIALSETUP.GERMINATEBASE_ID.eq(GROUPMEMBERS.FOREIGN_ID))
								 .and(GROUPMEMBERS.GROUP_ID.eq(GROUPS.ID))
								 .and(TRIALSETUP.DATASET_ID.in(requestedIds)));
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
