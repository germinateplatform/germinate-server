package jhi.germinate.server.resource.story;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableStories.VIEW_TABLE_STORIES;

@Path("story/table")
@Secured
@PermitAll
public class StoryTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableStoriesEnriched>> postStoryTable(PaginatedRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		HashSet<Integer> datasetsForUser = new HashSet<>(DatasetTableResource.getDatasetIdsForUser(req, userDetails, null, true));

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_STORIES)
													 .where(VIEW_TABLE_STORIES.STORY_VISIBILITY.eq(true)
																							   .or(VIEW_TABLE_STORIES.STORY_USER_ID.eq(userDetails.getId())));

			// Filter here!
			filter(from, filters, true);

			List<ViewTableStoriesEnriched> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableStoriesEnriched.class);

			result.forEach(s -> {
				if (s.getStoryRequirements() != null && !CollectionUtils.isEmpty(s.getStoryRequirements().getDatasetIds()))
					s.setCanAccess(datasetsForUser.containsAll(s.getStoryRequirements().getDatasetIds()));
				else
					s.setCanAccess(true);
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
