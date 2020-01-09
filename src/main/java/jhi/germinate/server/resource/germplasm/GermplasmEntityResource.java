package jhi.germinate.server.resource.germplasm;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;

import jhi.germinate.server.Database;

import static jhi.germinate.server.database.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmEntityResource extends ServerResource
{
	public static final String PARAM_DIRECTION = "direction";

	private String direction;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		direction = getQueryValue(PARAM_DIRECTION);
	}

	@Post("json")
	public List<Integer> postIds(Integer[] ids)
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			if (ids == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

			if (Objects.equals(direction, "down"))
			{
				return context.selectDistinct(GERMINATEBASE.ID)
							  .from(GERMINATEBASE)
							  .where(GERMINATEBASE.ENTITYPARENT_ID.in(ids))
							  .fetchInto(Integer.class);
			}
			else if (Objects.equals(direction, "up"))
			{
				return context.selectDistinct(GERMINATEBASE.ENTITYPARENT_ID)
							  .from(GERMINATEBASE)
							  .where(GERMINATEBASE.ENTITYPARENT_ID.isNotNull())
							  .and(GERMINATEBASE.ID.in(ids))
							  .fetchInto(Integer.class);
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
