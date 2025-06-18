package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.TraitTimelineRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("dataset/data/trial/timepoint")
@Secured
@PermitAll
public class TrialsDataTimepointResource extends ContextResource
{
	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> postDatasetTrialTimepoints(TraitTimelineRequest request)
			throws IOException, SQLException
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, "trials", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Field<String> field = DSL.field("DATE_FORMAT({0}, {1})", SQLDataType.VARCHAR, PHENOTYPEDATA.RECORDING_DATE, DSL.inline("%Y-%m-%d"));
			SelectConditionStep<Record1<String>> step = context.selectDistinct(field)
															   .from(PHENOTYPEDATA).leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
															   .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
															   .where(PHENOTYPEDATA.RECORDING_DATE.isNotNull())
															   .and(TRIALSETUP.DATASET_ID.in(requestedIds));

			// Handle requested germplasm ids or group ids
			Set<Integer> germplasmIds = new HashSet<>();
			if (!CollectionUtils.isEmpty(request.getGroupIds()))
				germplasmIds.addAll(context.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.ID.in(request.getGroupIds())).fetchInto(Integer.class));
			if (!CollectionUtils.isEmpty(request.getMarkedIds()))
				germplasmIds.addAll(request.getMarkedIds());
			if (!CollectionUtils.isEmpty(germplasmIds))
				step.and(TRIALSETUP.GERMINATEBASE_ID.in(germplasmIds));

			// Handle requested traits
			if (!CollectionUtils.isEmpty(request.getTraitIds()))
				step.and(PHENOTYPES.ID.in(request.getTraitIds()));

			return step
					.orderBy(field)
					.fetchInto(String.class);
		}
	}
}
