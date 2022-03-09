package jhi.germinate.server.resource.compound;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableCompounds;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Compounds.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.Units.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableCompounds.*;

@Path("dataset/compound")
@Secured
@PermitAll
public class DatasetCompoundResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableCompounds> getJson(DatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, "compound");
		List<Integer> requestedIds;

		if (CollectionUtils.isEmpty(request.getDatasetIds()))
		{
			requestedIds = datasets;
		}
		else
		{
			requestedIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
			requestedIds.retainAll(datasets);
		}

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
	}
}
