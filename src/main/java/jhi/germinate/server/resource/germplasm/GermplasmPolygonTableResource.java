package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.locations.LocationPolygonTableResource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class GermplasmPolygonTableResource extends GermplasmBaseResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableGermplasm>> getJson(PaginatedPolygonRequest request)
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectConditionStep<?> from = getGermplasmQuery(context)
				.where(DSL.field(LATITUDE).isNotNull()
						  .and(DSL.field(LONGITUDE).isNotNull())
						  .and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `longitude`, ' ', `latitude`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, adjustFilter(filters));

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
