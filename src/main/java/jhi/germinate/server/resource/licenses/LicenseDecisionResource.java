package jhi.germinate.server.resource.licenses;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Licenselogs;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Licenselogs.LICENSELOGS;

@Path("license/{licenseId}/accept")
@Secured
@PermitAll
public class LicenseDecisionResource extends ContextResource
{
	@PathParam("licenseId")
	Integer licenseId;

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteLicenseDecision(@Context HttpServletResponse response)
			throws SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (licenseId == null)
			throw new NotFoundException();

		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				int result = context.deleteFrom(LICENSELOGS)
									.where(LICENSELOGS.LICENSE_ID.eq(licenseId))
									.and(LICENSELOGS.USER_ID.eq(userDetails.getId()))
									.execute();

				AuthorizationFilter.ensureUserDatasetsAvailable(req, userDetails);
				return result > 0;
			}
		}
		else
		{
			AuthenticationFilter.updateAcceptedDatasets(req, response, licenseId, false);
			AuthorizationFilter.ensureUserDatasetsAvailable(req, userDetails);
			return true;
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean getLicenseDecision(@Context HttpServletResponse response)
			throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (licenseId == null)
			throw new NotFoundException();

		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				Licenselogs exists = context.selectFrom(LICENSELOGS)
											.where(LICENSELOGS.LICENSE_ID.eq(licenseId))
											.and(LICENSELOGS.USER_ID.eq(userDetails.getId()))
											.fetchAnyInto(Licenselogs.class);

				if (exists == null)
				{
					context.insertInto(LICENSELOGS)
						   .set(LICENSELOGS.LICENSE_ID, licenseId)
						   .set(LICENSELOGS.USER_ID, userDetails.getId())
						   .execute();

					AuthorizationFilter.ensureUserDatasetsAvailable(req, userDetails);

					return true;
				}
				else
				{
					throw new NoContentException("");
				}
			}
		}
		else
		{
			AuthenticationFilter.updateAcceptedDatasets(req, response, licenseId, true);
			AuthorizationFilter.ensureUserDatasetsAvailable(req, userDetails);
			return true;
		}
	}
}
