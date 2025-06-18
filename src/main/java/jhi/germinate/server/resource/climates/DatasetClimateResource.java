package jhi.germinate.server.resource.climates;

import jhi.germinate.resource.DatasetRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.MapoverlaysReferenceTable;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableClimates;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Mapoverlays.*;
import static jhi.germinate.server.database.codegen.tables.Units.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimates.*;

@Path("dataset/climate")
@Secured
@PermitAll
public class DatasetClimateResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;

	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableClimates> getJson(DatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		List<Integer> requestedIds = AuthorizationFilter.restrictDatasetIds(req, "climate", request.getDatasetIds(), true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			return context.select(
				CLIMATES.ID.as(VIEW_TABLE_CLIMATES.CLIMATE_ID.getName()),
				CLIMATES.NAME.as(VIEW_TABLE_CLIMATES.CLIMATE_NAME.getName()),
				CLIMATES.SHORT_NAME.as(VIEW_TABLE_CLIMATES.CLIMATE_NAME_SHORT.getName()),
				CLIMATES.DESCRIPTION.as(VIEW_TABLE_CLIMATES.CLIMATE_DESCRIPTION.getName()),
				CLIMATES.DATATYPE.as(VIEW_TABLE_CLIMATES.DATA_TYPE.getName()),
				UNITS.ID.as(VIEW_TABLE_CLIMATES.UNIT_ID.getName()),
				UNITS.UNIT_NAME.as(VIEW_TABLE_CLIMATES.UNIT_NAME.getName()),
				UNITS.UNIT_DESCRIPTION.as(VIEW_TABLE_CLIMATES.UNIT_DESCRIPTION.getName()),
				UNITS.UNIT_ABBREVIATION.as(VIEW_TABLE_CLIMATES.UNIT_ABBREVIATION.getName()),
				DSL.selectCount().from(MAPOVERLAYS).where(MAPOVERLAYS.REFERENCE_TABLE.eq(MapoverlaysReferenceTable.climates)).and(MAPOVERLAYS.FOREIGN_ID.eq(CLIMATES.ID)).asField(VIEW_TABLE_CLIMATES.OVERLAYS.getName()),
				DSL.zero().as(VIEW_TABLE_CLIMATES.COUNT.getName())
			)
						  .from(CLIMATES.leftJoin(UNITS).on(CLIMATES.UNIT_ID.eq(UNITS.ID)))
						  .where(DSL.exists(DSL.selectOne().from(CLIMATEDATA)
											   .where(CLIMATEDATA.DATASET_ID.in(requestedIds))
											   .and(CLIMATEDATA.CLIMATE_ID.eq(CLIMATES.ID))
											   .limit(1)))
						  .orderBy(CLIMATES.NAME)
						  .fetchInto(ViewTableClimates.class);
		}
	}
}
