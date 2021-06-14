package jhi.germinate.server.util;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.sql.SQLException;
import java.util.logging.Logger;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>
{
	@Override
	public Response toResponse(SQLException e)
	{
		e.printStackTrace();
		Logger.getLogger("").severe(e.getMessage());

		return Response.serverError()
					   .entity(e.getMessage())
					   .type(MediaType.TEXT_PLAIN)
					   .build();
	}
}
