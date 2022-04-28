package jhi.germinate.server.util;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter
{

	/**
	 * Method for ContainerRequestFilter.
	 */
	@Override
	public void filter(ContainerRequestContext request)
		throws IOException
	{
		// If it's a preflight request, we abort the request with
		// a 200 status, and the CORS headers are added in the
		// response filter method below.
		if (isPreflightRequest(request))
			request.abortWith(Response.ok().build());
	}

	/**
	 * A preflight request is an OPTIONS request
	 * with an Origin header.
	 */
	private static boolean isPreflightRequest(ContainerRequestContext request)
	{
		return request.getHeaderString("Origin") != null && request.getMethod().equalsIgnoreCase("OPTIONS");
	}

	/**
	 * Method for ContainerResponseFilter.
	 */
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response)
		throws IOException
	{
		// if there is no Origin header, then it is not a
		// cross origin request. We don't do anything.
		if (request.getHeaderString("Origin") == null)
			return;

		// If it is a preflight request, then we add all
		// the CORS headers here.
		if (isPreflightRequest(request))
		{
			response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS, HEAD");
			response.getHeaders().add("Access-Control-Allow-Headers",
				// Whatever other non-standard/safe headers (see list above)
				// you want the client to be able to send to the server,
				// put it in this list. And remove the ones you don't want.
				"X-Requested-With, Authorization, Accept-Version, Accept-Language, Content-MD5, CSRF-Token, Content-Type");
		}

		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Origin", request.getHeaderString("Origin"));
	}
}