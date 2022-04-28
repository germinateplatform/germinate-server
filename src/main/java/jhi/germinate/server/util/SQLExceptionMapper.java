package jhi.germinate.server.util;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Maps {@link SQLException}s to a {@link Response} that can be returned to the client.
 */
@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>
{
	@Override
	public Response toResponse(SQLException e)
	{
		// Log it
		e.printStackTrace();
		Logger.getLogger("").severe(e.getMessage());

		// Then return it
		return Response.serverError()
					   .entity(e.getMessage())
					   .type(MediaType.TEXT_PLAIN)
					   .build();
	}
}
