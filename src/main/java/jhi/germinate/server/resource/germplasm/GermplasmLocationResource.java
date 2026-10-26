package jhi.germinate.server.resource.germplasm;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Locations;
import jhi.germinate.server.database.codegen.tables.records.LocationsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;

@Path("germplasm/{germplasmId}/location")
@Secured(UserType.DATA_CURATOR)
public class GermplasmLocationResource extends ContextResource
{
	@PathParam("germplasmId")
	private Integer germplasmId;

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchGermplasmLocation(Locations newLocation)
			throws SQLException
	{
		if (germplasmId == null || newLocation == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			if (newLocation.getId() != null)
			{
				// It exists
				return context.update(GERMINATEBASE)
				              .set(GERMINATEBASE.LOCATION_ID, newLocation.getId())
				              .where(GERMINATEBASE.ID.eq(germplasmId))
				              .execute() > 0;
			}
			else
			{
				// It needs to be created
				if (newLocation.getCountryId() == null || StringUtils.isEmpty(newLocation.getSiteName()))
					throw new BadRequestException();

				try
				{
					LocationsRecord l = context.newRecord(LOCATIONS, newLocation);
					l.store();

					return context.update(GERMINATEBASE)
					              .set(GERMINATEBASE.LOCATION_ID, l.getId())
					              .where(GERMINATEBASE.ID.eq(germplasmId))
					              .execute() > 0;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Logger.getLogger("").info(e.getLocalizedMessage());
					throw new BadRequestException();
				}
			}
		}
	}
}
