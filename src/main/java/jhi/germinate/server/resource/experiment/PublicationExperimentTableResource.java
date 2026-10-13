package jhi.germinate.server.resource.experiment;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePublications;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.VIEW_TABLE_PUBLICATIONS;

@Path("publication/{publicationId}/experiment")
@Secured
@PermitAll
public class PublicationExperimentTableResource extends BaseExperimentTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableExperiments>> postPublicationExperimentTable(UnacceptedLicenseRequest request, @PathParam("publicationId") Integer publicationId)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
											   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
											   .and(VIEW_TABLE_PUBLICATIONS.EXPERIMENT_IDS.isNotNull())
											   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
				return new PaginatedResult<>(new ArrayList<>(), 0);

			Integer[] ids = pub.getExperimentIds();

			return runQuery(request, query -> query.where(EXPERIMENTS.ID.as("experiment_id").in(ids)));
		}
	}
}
