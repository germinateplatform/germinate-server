package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;
import static jhi.germinate.server.database.codegen.tables.Treatments.TREATMENTS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("dataset/data/trial/setup")
@Secured
@PermitAll
public class TrialSetupResource extends TrialsDataBaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TrialSetupStats postTrialSetupStats(DatasetRequest request)
			throws SQLException
	{
		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Integer> years = context.selectDistinct(DSL.year(PHENOTYPEDATA.RECORDING_DATE))
			                             .from(PHENOTYPEDATA)
			                             .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
			                             .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                             .and(PHENOTYPEDATA.RECORDING_DATE.isNotNull())
			                             .fetchInto(Integer.class);
			List<Treatments> treatments = context.selectDistinct(TREATMENTS.fields())
			                                     .from(TRIALSETUP)
			                                     .leftJoin(TREATMENTS).on(TREATMENTS.ID.eq(TRIALSETUP.TREATMENT_ID))
			                                     .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                                     .and(TRIALSETUP.TREATMENT_ID.isNotNull())
			                                     .fetchInto(Treatments.class);
			List<String> reps = context.selectDistinct(TRIALSETUP.REP)
			                           .from(TRIALSETUP)
			                           .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                           .and(TRIALSETUP.REP.isNotNull())
			                           .and(TRIALSETUP.REP.notEqual(""))
			                           .fetchInto(String.class);
			List<Taxonomies> taxonomies = context.selectDistinct(TAXONOMIES.fields())
			                                     .from(TRIALSETUP)
			                                     .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
			                                     .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
			                                     .where(TRIALSETUP.DATASET_ID.in(requestedIds))
			                                     .and(GERMINATEBASE.TAXONOMY_ID.isNotNull())
			                                     .fetchInto(Taxonomies.class);
			List<TrialCreationDetails.PlotDetails> plots = context.selectDistinct(
																		  TRIALSETUP.TRIAL_ROW,
																		  TRIALSETUP.TRIAL_COLUMN,
																		  GERMINATEBASE.DISPLAY_NAME,
																		  TRIALSETUP.REP
																  )
			                                                      .from(TRIALSETUP)
			                                                      .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
			                                                      .where(TRIALSETUP.TRIAL_ROW.isNotNull())
			                                                      .and(TRIALSETUP.TRIAL_COLUMN.isNotNull())
			                                                      .and(TRIALSETUP.DATASET_ID.in(requestedIds))
			                                                      .stream()
			                                                      .map(r -> new TrialCreationDetails.PlotDetails(r.get(TRIALSETUP.TRIAL_ROW) + "|" + r.get(TRIALSETUP.TRIAL_COLUMN), r.get(TRIALSETUP.TRIAL_ROW), r.get(TRIALSETUP.TRIAL_COLUMN), r.get(GERMINATEBASE.DISPLAY_NAME), r.get(TRIALSETUP.REP)))
			                                                      .toList();

			return new TrialSetupStats(reps, treatments, taxonomies, plots, years);
		}
	}
}
