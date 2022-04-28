package jhi.germinate.server.resource.licenses;

import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Licenselogs.*;

@Path("license/{licenseId}/accept")
@Secured
@PermitAll
public class LicenseDecisionResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean getLicenseDecision(@PathParam("licenseId") Integer licenseId)
		throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (licenseId == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return false;
		}

		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);
				context.insertInto(LICENSELOGS)
					   .set(LICENSELOGS.LICENSE_ID, licenseId)
					   .set(LICENSELOGS.USER_ID, userDetails.getId())
					   .execute();
			}
		}
		else
		{
			AuthenticationFilter.updateAcceptedDatasets(req, resp, licenseId);
		}

		return true;
	}
}
