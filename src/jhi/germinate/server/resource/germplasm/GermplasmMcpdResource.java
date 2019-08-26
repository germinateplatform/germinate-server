package jhi.germinate.server.resource.germplasm;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewMcpd;

import static jhi.germinate.server.database.tables.ViewMcpd.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmMcpdResource extends ServerResource
{
	private Integer germplasmId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.germplasmId = Integer.parseInt(getRequestAttributes().get("germplasmId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public ViewMcpd getJson()
	{
		if (germplasmId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			return context.selectFrom(VIEW_MCPD)
						  .where(VIEW_MCPD.ID.eq(germplasmId))
						  .fetchOneInto(ViewMcpd.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
