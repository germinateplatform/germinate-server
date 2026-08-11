package jhi.germinate.server.util;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

import java.util.logging.Logger;

/**
 * Maps {@link StatusException}s to a {@link Response} that can be returned to the client.
 */
@Provider
public class StatusExceptionMapper implements ExceptionMapper<StatusException>
{
	@Override
	public Response toResponse(StatusException e)
	{
		// Log it
		e.printStackTrace();
		String message = e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.";

		Logger.getLogger("").severe(message);

		return Response.status(e.getStatusCode())
		               .entity(e.getEntity() == null ? message : e.getEntity())
		               .type(MediaType.TEXT_PLAIN)
		               .build();
	}
}
