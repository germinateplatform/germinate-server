package jhi.germinate.server.resource.licenses;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLicenses;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jooq.Record;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableLicenses.*;

@Path("license/table")
@Secured
@PermitAll
public class LicenseTableResource extends BaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableLicenses>> postLicenseTable(PaginatedRequest request)
		throws SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_LICENSES);
			// Filter here!
			where(from, filters);

			List<ViewTableLicenses> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableLicenses.class);

			Set<Integer> acceptedLicenses = AuthenticationFilter.getAcceptedLicenses(req);

			result.forEach(d -> {
				Integer[] acceptedBy = d.getAcceptedBy();
				if (mode == AuthenticationMode.NONE)
				{
					// If there's no authentication, check if the license is in the cookie
					if (acceptedLicenses.contains(d.getLicenseId()))
					{
						acceptedBy = new Integer[1];
						acceptedBy[0] = userDetails.getId();
						d.setAcceptedBy(acceptedBy);
					}
					else
					{
						d.setAcceptedBy(new Integer[0]);
					}
				}
				else if (mode == AuthenticationMode.SELECTIVE)
				{
					if (userDetails.getId() == -1000)
					{
						// If we offer login, but the user hasn't logged in, check the cookie
						if (acceptedLicenses.contains(d.getLicenseId()))
						{
							acceptedBy = new Integer[1];
							acceptedBy[0] = userDetails.getId();
							d.setAcceptedBy(acceptedBy);
						}
						else
						{
							d.setAcceptedBy(new Integer[0]);
						}
					}
					else
					{
						if (acceptedBy != null)
						{
							List<Integer> ids = Arrays.asList(acceptedBy);
							if (ids.contains(userDetails.getId()))
							{
								// If the user accepted the license, set his/her id to indicate this
								acceptedBy = new Integer[1];
								acceptedBy[0] = userDetails.getId();
								d.setAcceptedBy(acceptedBy);
							}
							else
							{
								// Else, clear this information
								d.setAcceptedBy(new Integer[0]);
							}
						}
						else
						{
							// Else, clear this information
							d.setAcceptedBy(new Integer[0]);
						}
					}
				}
				else
				{
					if (acceptedBy != null)
					{
						List<Integer> ids = Arrays.asList(acceptedBy);
						if (ids.contains(userDetails.getId()))
						{
							// If the user accepted the license, set his/her id to indicate this
							acceptedBy = new Integer[1];
							acceptedBy[0] = userDetails.getId();
							d.setAcceptedBy(acceptedBy);
						}
						else
						{
							// Else, clear this information
							d.setAcceptedBy(new Integer[0]);
						}
					}
					else
					{
						// Else, clear this information
						d.setAcceptedBy(new Integer[0]);
					}
				}
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
