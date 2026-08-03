package jhi.germinate.server.resource.licenses;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLicenseDefinitions;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Licensedata.*;
import static jhi.germinate.server.database.codegen.tables.Licenses.*;
import static jhi.germinate.server.database.codegen.tables.Locales.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableLicenseDefinitions.*;

@Path("license")
public class LicenseResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<ViewTableLicenseDefinitions> getLicenses()
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(VIEW_TABLE_LICENSE_DEFINITIONS).fetchInto(ViewTableLicenseDefinitions.class);
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Response addLicenseAndData(ViewTableLicenseDefinitions license)
		throws IOException, SQLException
	{
		if (license == null || StringUtils.isEmpty(license.getLicenseName()))
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid payload parameters").build();
		}

		if (license.getLicenseId() != null) {
			return Response.status(Response.Status.CONFLICT.getStatusCode(), "License ID provided on a creation call.").build();
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Create a new license record
			LicensesRecord l = context.newRecord(LICENSES);
			l.setName(license.getLicenseName());
			l.setDescription(license.getLicenseDescription());
			if (license.getCreatedOn() != null)
			{
				l.setCreatedOn(license.getCreatedOn());
			}
			l.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			l.store();

			for (Map.Entry<String, String> entry : license.getLicenseData().entrySet())
			{
				// Get or create the locale
				LocalesRecord lo = context.selectFrom(LOCALES).where(LOCALES.NAME.eq(entry.getKey())).fetchAny();

				if (lo == null)
				{
					lo = context.newRecord(LOCALES);
					lo.setName(entry.getKey());
					lo.setDescription(entry.getKey());
					lo.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					lo.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
					lo.store();
				}

				// Create a new license data record
				LicensedataRecord ld = context.newRecord(LICENSEDATA);
				ld.setLicenseId(l.getId());
				ld.setLocaleId(lo.getId());
				ld.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				ld.setContent(entry.getValue());
				ld.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
				ld.store();
			}

			return Response.ok(l.getId()).build();
		}
	}

	@PATCH
	@Path("/{licenseId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Response patchLicense(@PathParam("licenseId") Integer licenseId, ViewTableLicenseDefinitions license)
		throws IOException, SQLException
	{
		if (licenseId == null || license == null || !licenseId.equals(license.getLicenseId()) || StringUtils.isEmpty(license.getLicenseName()))
		{
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid payload parameters").build();
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			LicensesRecord l = context.selectFrom(LICENSES).where(LICENSES.ID.eq(licenseId)).fetchAny();

			if (l == null)
			{
				return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
			}

			// Update the license
			l.setName(license.getLicenseName());
			l.setDescription(license.getLicenseDescription());
			if (license.getCreatedOn() != null)
			{
				l.setCreatedOn(license.getCreatedOn());
			}
			l.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			l.store();

			for (Map.Entry<String, String> entry : license.getLicenseData().entrySet())
			{
				LocalesRecord lo = context.selectFrom(LOCALES).where(LOCALES.NAME.eq(entry.getKey())).fetchAny();

				if (lo == null) {
					lo = context.newRecord(LOCALES);
					lo.setName(entry.getKey());
					lo.setDescription(entry.getKey());
					lo.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					lo.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
					lo.store();
				}

				LicensedataRecord ld = context.selectFrom(LICENSEDATA)
											  .where(LICENSEDATA.LICENSE_ID.eq(licenseId))
											  .and(LICENSEDATA.LOCALE_ID.eq(lo.getId()))
											  .fetchAny();

				if (ld == null)
				{
					ld = context.newRecord(LICENSEDATA);
					ld.setLicenseId(licenseId);
					ld.setLocaleId(lo.getId());
					ld.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				}

				ld.setContent(entry.getValue());
				ld.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
				ld.store();
			}
		}

		return Response.ok(true).build();
	}
}
