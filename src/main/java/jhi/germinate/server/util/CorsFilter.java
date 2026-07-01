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
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		if (isPreflightRequest(request)) {
			Response.ResponseBuilder response = Response.ok();
			addCorsHeaders(request, response, true);
			request.abortWith(response.build());
		}
	}

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		if (request.getHeaderString("Origin") == null) return;

		// Don't re-add headers to aborted preflight responses — they're already set
		if (isPreflightRequest(request)) return;

		response.getHeaders().putSingle("Access-Control-Allow-Origin", request.getHeaderString("Origin"));
		response.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
		response.getHeaders().putSingle("Vary", "Origin");
	}

	private void addCorsHeaders(ContainerRequestContext request, Response.ResponseBuilder builder, boolean isPreflight) {
		builder.header("Access-Control-Allow-Origin", request.getHeaderString("Origin"));
		builder.header("Access-Control-Allow-Credentials", "true");
		builder.header("Vary", "Origin");
		if (isPreflight) {
			builder.header("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS, HEAD");
			builder.header("Access-Control-Allow-Headers",
					"X-Requested-With, Authorization, Accept-Version, Accept-Language, Content-MD5, CSRF-Token, Content-Type");
			builder.header("Access-Control-Max-Age", "86400");
		}
	}

	private static boolean isPreflightRequest(ContainerRequestContext request) {
		return request.getHeaderString("Origin") != null
				&& request.getMethod().equalsIgnoreCase("OPTIONS");
	}
}