package jhi.germinate.server.resource.locations;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import jhi.germinate.server.database.codegen.tables.records.LocationsRecord;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.ViewTableLocations.VIEW_TABLE_LOCATIONS;

@Path("location/{locationId:\\d+}")
@Secured(UserType.DATA_CURATOR)
public class LocationResource
{
	@PathParam("locationId")
	Integer locationId;

	@PATCH
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ViewTableLocations patchLocation(ViewTableLocations location)
			throws SQLException
	{
		if (location == null || location.getLocationId() == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			LocationsRecord record = context.selectFrom(LOCATIONS)
											.where(LOCATIONS.ID.eq(location.getLocationId()))
											.fetchAny();

			if (record == null)
				throw new NotFoundException();

			record.setSiteName(location.getLocationName());
			record.setLatitude(location.getLocationLatitude());
			record.setLongitude(location.getLocationLongitude());
			record.setElevation(location.getLocationElevation());
			record.store();

			return context.selectFrom(VIEW_TABLE_LOCATIONS)
						  .where(VIEW_TABLE_LOCATIONS.LOCATION_ID.eq(locationId))
						  .fetchAnyInto(ViewTableLocations.class);
		}
	}
}
