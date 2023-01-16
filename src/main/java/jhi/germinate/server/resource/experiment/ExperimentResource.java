package jhi.germinate.server.resource.experiment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.ViewTableExperiments;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.ExperimentsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Experiments.*;

@Path("experiment")
public class ExperimentResource extends ContextResource
{
	@DELETE
	@Path("/{experimentId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteExperiment(@PathParam("experimentId") Integer experimentId)
		throws SQLException, IOException
	{
		if (experimentId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid payload parameters");
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ExperimentsRecord toDelete = context.selectFrom(EXPERIMENTS).where(EXPERIMENTS.ID.eq(experimentId)).fetchAny();

			if (toDelete == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			// Check if datasets have to be reassigned to a dummy dataset
			boolean hasDatasets = context.fetchExists(DSL.selectOne().from(DATASETS).where(DATASETS.EXPERIMENT_ID.eq(experimentId)));
			if (hasDatasets)
			{
				// If it doesn't, check if the experiment exists
				ExperimentsRecord dummy = context.selectFrom(EXPERIMENTS)
												 .where(EXPERIMENTS.EXPERIMENT_NAME.isNotDistinctFrom("Generic Experiment"))
												 .and(EXPERIMENTS.DESCRIPTION.isNotDistinctFrom("A generic experiment to which every uploaded dataset will be assigned initially."))
												 .fetchAny();

				if (dummy == null)
				{
					// If it doesn't, create it
					dummy = context.newRecord(EXPERIMENTS);
					dummy.setExperimentName("Generic Experiment");
					dummy.setDescription("A generic experiment to which every uploaded dataset will be assigned initially.");
					dummy.store();
				}

				context.update(DATASETS)
					   .set(DATASETS.EXPERIMENT_ID, dummy.getId())
					   .where(DATASETS.EXPERIMENT_ID.eq(experimentId))
					   .execute();
			}

			return toDelete.delete() > 0;
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Integer putExperiment(ViewTableExperiments experiment)
		throws IOException, SQLException
	{
		if (experiment == null || StringUtils.isEmpty(experiment.getExperimentName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid payload parameters");
			return null;
		}

		if (experiment.getExperimentId() != null)
		{
			resp.sendError(Response.Status.CONFLICT.getStatusCode(), "Experiment ID provided on a creation call.");
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Create a new experiment record
			ExperimentsRecord e = context.newRecord(EXPERIMENTS);
			e.setExperimentName(experiment.getExperimentName());
			e.setDescription(experiment.getExperimentDescription());
			if (experiment.getCreatedOn() != null)
				e.setCreatedOn(experiment.getCreatedOn());
			e.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			e.store();

			return e.getId();
		}
	}

	@PATCH
	@Path("/{experimentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean patchExperiment(@PathParam("experimentId") Integer experimentId, ViewTableExperiments experiment)
		throws IOException, SQLException
	{
		if (experimentId == null || experiment == null || !experimentId.equals(experiment.getExperimentId()) || StringUtils.isEmpty(experiment.getExperimentName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid payload parameters");
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ExperimentsRecord e = context.selectFrom(EXPERIMENTS).where(EXPERIMENTS.ID.eq(experimentId)).fetchAny();

			if (e == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			// Update the experiment
			e.setExperimentName(experiment.getExperimentName());
			e.setDescription(experiment.getExperimentDescription());
			if (experiment.getCreatedOn() != null)
				e.setCreatedOn(experiment.getCreatedOn());
			e.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			e.store();
		}

		return true;
	}
}
