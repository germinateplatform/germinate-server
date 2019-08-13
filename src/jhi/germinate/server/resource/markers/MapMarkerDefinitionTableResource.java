package jhi.germinate.server.resource.markers;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.gatekeeper.resource.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class MapMarkerDefinitionTableResource extends PaginatedServerResource implements FilteredResource
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

	@Post("json")
	public PaginatedResult<List<ViewTableMapdefinitions>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest());

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq((byte) 1)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USERID.eq(userDetails.getId())))
				.and(VIEW_TABLE_MAPDEFINITIONS.MAPID.eq(mapId));

			// Filter here!
			filter(from, filters);

			List<ViewTableMapdefinitions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableMapdefinitions.class);

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
