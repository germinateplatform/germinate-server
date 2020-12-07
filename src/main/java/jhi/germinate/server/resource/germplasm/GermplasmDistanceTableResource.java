package jhi.germinate.server.resource.germplasm;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class GermplasmDistanceTableResource extends GermplasmBaseResource
{
	@Post("json")
	public PaginatedResult<List<GermplasmDistance>> getJson(PaginatedLocationRequest request)
	{
		if (request.getLatitude() == null || request.getLongitude() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			Field<BigDecimal> dLat = DSL.rad(DSL.field(LATITUDE, BigDecimal.class).minus(request.getLatitude()));
			Field<BigDecimal> dLon = DSL.rad(DSL.field(LONGITUDE, BigDecimal.class).minus(request.getLongitude()));

			Field<BigDecimal> a = DSL.power(DSL.sin(dLon.div(2)), 2)
									 .times(DSL.cos(DSL.rad(request.getLatitude())))
									 .times(DSL.cos(DSL.rad(DSL.field(LATITUDE, BigDecimal.class))))
									 .plus(DSL.power(DSL.sin(dLat.div(2)), 2));

			Field<BigDecimal> c = DSL.asin(DSL.sqrt(a)).times(2);

			SelectConditionStep<?> from = getGermplasmQuery(context, DSL.cast(c.times(6372.8), Double.class).as("distance"))
				.where(DSL.field(LONGITUDE).isNotNull())
				.and(DSL.field(LATITUDE).isNotNull());

			// Filter here!
			filter(from, adjustFilter(filters));

			List<GermplasmDistance> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(GermplasmDistance.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
