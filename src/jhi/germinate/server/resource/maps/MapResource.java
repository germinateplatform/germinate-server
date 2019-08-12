package jhi.germinate.server.resource.maps;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.Maps.*;

/**
 * @author Sebastian Raubach
 */
public class MapResource extends PaginatedServerResource
{
	@Get("json")
	public PaginatedResult<List<Maps>> getJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(MAPS);

			from.where(MAPS.VISIBILITY.eq((byte) 1)
									  .or(MAPS.USER_ID.eq(userDetails.getId())));

			List<Maps> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Maps.class);

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
