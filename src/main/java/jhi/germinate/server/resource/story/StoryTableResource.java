package jhi.germinate.server.resource.story;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Storysteps;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.ViewTableStories.VIEW_TABLE_STORIES;

@Path("story/table")
@Secured
@PermitAll
public class StoryTableResource extends BaseResource
{
	@POST
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableStoriesEnriched>> postStoryTable(PaginatedRequest request)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		HashSet<Integer> datasetsForUser = new HashSet<>(AuthorizationFilter.getDatasetIds(req, null, true));

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
			where(from, filters, true);

			List<ViewTableStoriesEnriched> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableStoriesEnriched.class);

			result.forEach(s -> {
				if (s.getStoryRequirements() != null && !CollectionUtils.isEmpty(s.getStoryRequirements().getDatasetIds()))
					s.setCanAccess(datasetsForUser.containsAll(s.getStoryRequirements().getDatasetIds()));
				else
					s.setCanAccess(true);

				if (!CollectionUtils.isEmpty(s.getStorySteps()))
				{
					for (Storysteps st : s.getStorySteps())
					{
						if (!StringUtils.isEmpty(st.getDescription())) {
							String[] parts = st.getDescription().split("\n");

							st.setDescription(Arrays.stream(parts).filter(str -> !str.trim().isEmpty()).map(str -> "<p>" + str + "</p>").collect(Collectors.joining()));
						}
					}
				}
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
