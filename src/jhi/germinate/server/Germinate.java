package jhi.germinate.server;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.*;
import org.restlet.resource.*;
import org.restlet.routing.*;
import org.restlet.security.*;
import org.restlet.util.*;

import java.util.*;

import jhi.germinate.server.auth.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.germplasm.*;

/**
 * @author Sebastian Raubach
 */
public class Germinate extends Application
{
	private static CustomVerifier         verifier = new CustomVerifier();
	public Router routerAuth;
	private        ChallengeAuthenticator authenticator;
	private        MethodAuthorizer       authorizer;
	private Router routerUnauth;

	public Germinate()
	{
		// Set information about API
		setName("Germinate Server");
		setDescription("This is the server implementation for the Germinate");
		setOwner("The James Hutton Institute");
		setAuthor("Sebastian Raubach, Information & Computational Sciences");
	}

	private void setUpAuthentication(Context context)
	{
		authorizer = new MethodAuthorizer();
		authorizer.getAuthenticatedMethods().add(Method.GET);
		authorizer.getAuthenticatedMethods().add(Method.OPTIONS);
		authorizer.getAuthenticatedMethods().add(Method.PATCH);
		authorizer.getAuthenticatedMethods().add(Method.POST);
		authorizer.getAuthenticatedMethods().add(Method.PUT);
		authorizer.getAuthenticatedMethods().add(Method.DELETE);

		authenticator = new ChallengeAuthenticator(context, true, ChallengeScheme.HTTP_OAUTH_BEARER, "Gatekeeper", verifier);
	}

	@Override
	public Restlet createInboundRoot()
	{
		Context context = getContext();

		setUpAuthentication(context);

		// Set the encoder
//		Filter encoder = new Encoder(context, false, true, new EncoderService(true));

		// Create new router
		routerAuth = new Router(context);
		routerUnauth = new Router(context);

		// Set the Cors filter
		CorsFilter corsFilter = new CorsFilter(context, routerUnauth)
		{
			@Override
			protected int beforeHandle(Request request, Response response)
			{
				if (getCorsResponseHelper().isCorsRequest(request))
				{
					Series<Header> headers = request.getHeaders();

					for (Header header : headers)
					{
						if (header.getName().equalsIgnoreCase("origin"))
						{
							response.setAccessControlAllowOrigin(header.getValue());
						}
					}
				}
				return super.beforeHandle(request, response);
			}
		};
		corsFilter.setAllowedOrigins(new HashSet<>(Collections.singletonList("*")));
		corsFilter.setSkippingResourceForCorsOptions(true);
		corsFilter.setAllowingAllRequestedHeaders(true);
		corsFilter.setDefaultAllowedMethods(new HashSet<>(Arrays.asList(Method.POST, Method.GET, Method.PUT, Method.PATCH, Method.DELETE, Method.OPTIONS)));
		corsFilter.setAllowedCredentials(true);

		// Attach the url handlers
		attachToRouter(routerAuth, "/germplasm", GermplasmResource.class);
		attachToRouter(routerUnauth, "/token", TokenResource.class);

		// CORS first, then encoder
		corsFilter.setNext(routerUnauth);
		// After that the unauthorized paths
//		encoder.setNext(routerUnauth);
		// Set everything that isn't covered to go through the authenticator
		routerUnauth.attachDefault(authenticator);
		authenticator.setNext(authorizer);
		// And finally it ends up at the authenticated router
		authorizer.setNext(routerAuth);

		return corsFilter;
	}

	private void attachToRouter(Router router, String url, Class<? extends ServerResource> clazz)
	{
		router.attach(url, clazz);
		router.attach(url + "/", clazz);
	}
}
