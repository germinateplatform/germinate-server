package jhi.germinate.server.resource.maps;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;

/**
 * @author Sebastian Raubach
 */
public class MapChromosomeResource extends ServerResource
{
	private Integer mapId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.mapId = Integer.parseInt(getRequestAttributes().get("mapId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public List<String> getJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (mapId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			return context.selectDistinct(MAPDEFINITIONS.CHROMOSOME)
						  .from(MAPS)
						  .leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MAP_ID.eq(MAPS.ID))
						  .where(MAPS.ID.eq(mapId))
						  .and(MAPS.VISIBILITY.eq(true)
											  .or(MAPS.USER_ID.eq(userDetails.getId())))
						  .orderBy(MAPDEFINITIONS.CHROMOSOME)
						  .fetchInto(String.class);
		}
	}
}
