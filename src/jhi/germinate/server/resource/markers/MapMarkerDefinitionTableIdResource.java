package jhi.germinate.server.resource.markers;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.pojos.ViewTableMapdefinitions;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class MapMarkerDefinitionTableIdResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record1<Integer>> select = context.selectDistinct(VIEW_TABLE_MAPDEFINITIONS.MARKER_ID);

			SelectJoinStep<Record1<Integer>> from = select.from(VIEW_TABLE_MAPDEFINITIONS);

			from.where(VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true)
														   .or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
