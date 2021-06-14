package jhi.germinate.server.resource;

import javax.servlet.http.*;
import javax.ws.rs.core.*;

public class ContextResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;
	@Context
	protected HttpServletResponse resp;
}
