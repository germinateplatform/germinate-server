package jhi.germinate.server.resource.licenses;

import jakarta.annotation.security.PermitAll;
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
	public Response deleteLicenseDecision()
			throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (licenseId == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				return Response.ok(context.deleteFrom(LICENSELOGS)
											.where(LICENSELOGS.LICENSE_ID.eq(licenseId))
											.and(LICENSELOGS.USER_ID.eq(userDetails.getId()))
											.execute() > 0).build();
			}
		}
		else
		{
			AuthenticationFilter.updateAcceptedDatasets(req, resp, licenseId, false);
			return Response.ok(true).build();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicenseDecision()
			throws IOException, SQLException
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (licenseId == null)
			return Response.status(Response.Status.NOT_FOUND).build();

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

					return Response.ok(true).build();
				}
				else
				{
					return Response.status(Response.Status.NO_CONTENT).build();
				}
			}
		}
		else
		{
			AuthenticationFilter.updateAcceptedDatasets(req, resp, licenseId, true);
			return Response.ok(true).build();
		}
	}
}
