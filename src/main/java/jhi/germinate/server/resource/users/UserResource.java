package jhi.germinate.server.resource.users;

import jhi.germinate.resource.ViewUserDetailsType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.GatekeeperClient;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("user")
@Secured({UserType.ADMIN})
public class UserResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewUserDetailsType> getUser()
		throws SQLException
	{
		return GatekeeperClient.getUsers();
	}
}
