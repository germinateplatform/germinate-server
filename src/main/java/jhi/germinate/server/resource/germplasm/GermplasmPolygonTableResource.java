package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableGermplasm;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.locations.LocationPolygonTableResource;

import static jhi.germinate.server.database.tables.ViewTableGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmPolygonTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableGermplasm>> getJson(PaginatedPolygonRequest request)
	{
		if (request.getPolygons() == null || request.getPolygons().length < 1)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_GERMPLASM);

			from.where(VIEW_TABLE_GERMPLASM.LATITUDE.isNotNull()
													.and(VIEW_TABLE_GERMPLASM.LONGITUDE.isNotNull())
													.and(DSL.condition("ST_CONTAINS(ST_GeomFromText({0}), ST_GeomFromText (CONCAT( 'POINT(', `view_table_germplasm`.`longitude`, ' ', `view_table_germplasm`.`latitude`, ')')))", LocationPolygonTableResource.buildSqlPolygon(request.getPolygons()))));

			// Filter here!
			filter(from, filters);

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
