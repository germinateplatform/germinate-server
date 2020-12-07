package jhi.germinate.server.resource.license;

import com.google.gson.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLicenses;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.*;
import org.restlet.resource.Post;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableLicenses.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseTableResource extends PaginatedServerResource
{
	@Post("json")
	public PaginatedResult<List<ViewTableLicenses>> getJson(PaginatedRequest request)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (DSLContext context = Database.getContext())
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_LICENSES);
			// Filter here!
			filter(from, filters);

			List<ViewTableLicenses> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableLicenses.class);

			Set<Integer> acceptedLicenses = CustomVerifier.getAcceptedLicenses(getRequest());

			result.forEach(d -> {
					  JsonArray acceptedBy = d.getAcceptedBy();
					  JsonElement userId = new JsonParser().parse(Integer.toString(userDetails.getId()));
					  if (mode == AuthenticationMode.NONE)
					  {
						  // If there's no authentication, check if the license is in the cookie
						  if (acceptedLicenses.contains(d.getLicenseId()))
						  {
							  acceptedBy = new JsonArray();
							  acceptedBy.add(userId);
							  d.setAcceptedBy(acceptedBy);
						  }
						  else
						  {
							  d.setAcceptedBy(new JsonArray());
						  }
					  }
					  else if (mode == AuthenticationMode.SELECTIVE)
					  {
						  if (userDetails.getId() == -1000)
						  {
							  // If we offer login, but the user hasn't logged in, check the cookie
							  if (acceptedLicenses.contains(d.getLicenseId()))
							  {
								  acceptedBy = new JsonArray();
								  acceptedBy.add(userId);
								  d.setAcceptedBy(acceptedBy);
							  }
							  else
							  {
								  d.setAcceptedBy(new JsonArray());
							  }
						  }
						  else
						  {
							  if (acceptedBy != null && acceptedBy.contains(userId))
							  {
								  // If the user accepted the license, set his/her id to indicate this
								  acceptedBy = new JsonArray();
								  acceptedBy.add(userId);
								  d.setAcceptedBy(acceptedBy);
							  }
							  else
							  {
								  // Else, clear this information
								  d.setAcceptedBy(new JsonArray());
							  }
						  }
					  }
					  else
					  {
						  if (acceptedBy != null && acceptedBy.contains(userId))
						  {
							  // If the user accepted the license, set his/her id to indicate this
							  acceptedBy = new JsonArray();
							  acceptedBy.add(userId);
							  d.setAcceptedBy(acceptedBy);
						  }
						  else
						  {
							  // Else, clear this information
							  d.setAcceptedBy(new JsonArray());
						  }
					  }
				  });

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}