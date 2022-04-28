package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Locations;
import jhi.germinate.server.database.codegen.tables.records.LocationsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;

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
		throws SQLException, IOException
	{
		if (germplasmId == null || newLocation == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

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
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return false;
				}

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
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return false;
				}
			}
		}
	}
}
