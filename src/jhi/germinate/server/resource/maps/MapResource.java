package jhi.germinate.server.resource.maps;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.pojos.Maps;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.Maps.*;

/**
 * @author Sebastian Raubach
 */
public class MapResource extends PaginatedServerResource
{
	private Integer mapId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.mapId = Integer.parseInt(getRequestAttributes().get("mapId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public PaginatedResult<List<Maps>> getJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		if (userDetails == null)
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(MAPS);

			from.where(MAPS.VISIBILITY.eq((byte) 1)
									  .or(MAPS.USER_ID.eq(userDetails.getId())));

			if (mapId != null)
				from.where(MAPS.ID.eq(mapId));

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
