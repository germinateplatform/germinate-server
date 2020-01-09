package jhi.germinate.server.resource.germplasm;

import org.restlet.representation.Representation;
import org.restlet.resource.*;

import jhi.germinate.server.resource.BaseServerResource;

import static jhi.germinate.server.database.tables.ViewTableLocations.*;

/**
 * @author Sebastian Raubach
 */
public class GroupLocationFileAddResource extends BaseServerResource implements GroupAdditionInterface
{
	private Integer groupId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.groupId = Integer.parseInt(getRequestAttributes().get("groupId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	public int getJson(Representation entity)
	{
		return addGroupMembersFromFile(groupId, entity, VIEW_TABLE_LOCATIONS.LOCATION_ID, VIEW_TABLE_LOCATIONS, getRequest(), getResponse());
	}
}
