package jhi.germinate.server;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.CorsFilter;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.security.*;
import org.restlet.util.Series;

import java.util.*;

import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.*;
import jhi.germinate.server.resource.germplasm.*;
import jhi.germinate.server.resource.groups.*;
import jhi.germinate.server.resource.images.ImageSourceResource;
import jhi.germinate.server.resource.importers.*;
import jhi.germinate.server.resource.locations.*;
import jhi.germinate.server.resource.maps.*;
import jhi.germinate.server.resource.markers.*;
import jhi.germinate.server.resource.settings.*;
import jhi.germinate.server.resource.stats.OverviewStatsResource;

/**
 * @author Sebastian Raubach
 */
public class Germinate extends Application
{
	public static  Germinate              INSTANCE;
	private static CustomVerifier         verifier = new CustomVerifier();
	public         Router                 routerAuth;
	private        ChallengeAuthenticator authenticator;
	private        MethodAuthorizer       authorizer;
	private        Router                 routerUnauth;

	public Germinate()
	{
		// Set information about API
		setName("Germinate Server");
		setDescription("This is the server implementation for the Germinate");
		setOwner("The James Hutton Institute");
		setAuthor("Sebastian Raubach, Information & Computational Sciences");

		INSTANCE = this;
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
		// DATA IMPORT
		attachToRouter(routerAuth, "/import/template/mcpd", McpdImporterResource.class);
		attachToRouter(routerAuth, "/import/template/{uuid}/status", ImportStatusResource.class);

		// DATASETS
		attachToRouter(routerAuth, "/dataset/table", DatasetTableResource.class);
		attachToRouter(routerAuth, "/dataset/table/ids", DatasetTableIdResource.class);

		// GERMPLASM
		attachToRouter(routerAuth, "/germplasm", GermplasmResource.class);
		attachToRouter(routerAuth, "/germplasm/table", GermplasmTableResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/mcpd", GermplasmMcpdResource.class);

		// GROUPS
		attachToRouter(routerAuth, "/group/table", GroupTableResource.class);
		attachToRouter(routerAuth, "/group", GroupResource.class);
		attachToRouter(routerAuth, "/group/{groupId}", GroupResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm", GermplasmGroupTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm/ids", GermplasmGroupTableIdResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location", LocationGroupTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location/ids", LocationGroupTableIdResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker", MarkerGroupTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker/ids", MarkerGroupTableIdResource.class);
		attachToRouter(routerAuth, "/grouptype", GroupTypeResource.class);

		// IMAGES
//		attachToRouter(routerAuth, "/image", ImageResource.class);
		attachToRouter(routerAuth, "/image/{imageId}/src", ImageSourceResource.class);
		attachToRouter(routerAuth, "/image/src", ImageSourceResource.class);

		// LICENSES
		attachToRouter(routerAuth, "/license/table", LicenseTableResource.class);

		// LOCATIONS
		attachToRouter(routerAuth, "/location/table", LocationTableResource.class);
		attachToRouter(routerAuth, "/location/table/ids", LocationTableIdResource.class);

		// MAPS
		attachToRouter(routerAuth, "/map/table", MapTableResource.class);
		attachToRouter(routerAuth, "/map", MapResource.class);
		attachToRouter(routerAuth, "/map/{mapId}", MapResource.class);
		attachToRouter(routerAuth, "/map/{mapId}/export", MapExportResource.class);
		attachToRouter(routerAuth, "/map/{mapId}/mapdefinition/table", MapMarkerDefinitionTableResource.class);

		// MARKERS
		attachToRouter(routerAuth, "/marker/table", MarkerTableResource.class);

		// STATS
		attachToRouter(routerAuth, "/stats/overview", OverviewStatsResource.class);

		// SETTINGS
		attachToRouter(routerAuth, "/settings/write", SettingsWriterResource.class);
		attachToRouter(routerAuth, "/settings/file", SettingsFileResource.class);

		// UNAUTH
		attachToRouter(routerUnauth, "/clientlocale/{locale}", ClientLocaleResource.class);
		attachToRouter(routerUnauth, "/settings", SettingsResource.class);
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
