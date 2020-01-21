package jhi.germinate.server;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.CorsFilter;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.security.*;
import org.restlet.util.Series;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.attributes.*;
import jhi.germinate.server.resource.climates.*;
import jhi.germinate.server.resource.comment.*;
import jhi.germinate.server.resource.compounds.*;
import jhi.germinate.server.resource.datasets.*;
import jhi.germinate.server.resource.datasets.export.*;
import jhi.germinate.server.resource.entities.EntityTableResource;
import jhi.germinate.server.resource.experiment.ExperimentTableResource;
import jhi.germinate.server.resource.gatekeeper.*;
import jhi.germinate.server.resource.germplasm.*;
import jhi.germinate.server.resource.groups.*;
import jhi.germinate.server.resource.images.*;
import jhi.germinate.server.resource.importers.*;
import jhi.germinate.server.resource.institution.InstitutionTableResource;
import jhi.germinate.server.resource.license.*;
import jhi.germinate.server.resource.locations.*;
import jhi.germinate.server.resource.maps.*;
import jhi.germinate.server.resource.markers.*;
import jhi.germinate.server.resource.news.NewsTableResource;
import jhi.germinate.server.resource.pedigrees.*;
import jhi.germinate.server.resource.settings.*;
import jhi.germinate.server.resource.stats.*;
import jhi.germinate.server.resource.traits.*;
import jhi.germinate.server.resource.usergroups.*;
import jhi.germinate.server.resource.users.*;

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
		corsFilter.setExposedHeaders(Collections.singleton("Content-Disposition"));

		// Attach the url handlers
		// CLIMATE
		attachToRouter(routerAuth, "/climate/table", ClimateTableResource.class);
		attachToRouter(routerAuth, "/climate/overlay", ClimateOverlayResource.class);

		// COMMENTS
		attachToRouter(routerAuth, "/comment/table", CommentTableResource.class);
		attachToRouter(routerAuth, "/comment/{commentId}", CommentResource.class);
		attachToRouter(routerAuth, "/comment", CommentResource.class);

		// COMPOUNDS
		attachToRouter(routerAuth, "/compound/table", CompoundTableResource.class);

		// DATASETS
		attachToRouter(routerAuth, "/dataset/attribute/export", DatasetAttributeExportResource.class);
		attachToRouter(routerAuth, "/dataset/table", DatasetTableResource.class);
		attachToRouter(routerAuth, "/dataset/table/ids", DatasetTableIdResource.class);
		attachToRouter(routerAuth, "/dataset/table/export", DatasetTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/collaborator", CollaboratorTableResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/attribute", DatasetAttributeTableResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/attribute/export", DatasetAttributeTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/download-source", DatasetSourceDownloadResource.class);
		attachToRouter(routerAuth, "/dataset/attribute", DatasetAttributeTableResource.class);
//		attachToRouter(routerAuth, "/dataset/attribute/export", DatasetAttributeTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/compound", CompoundExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/climate", ClimateExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/allelefreq/histogram", AlleleFrequencyHistogramExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/allelefreq", AlleleFrequencyExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/trial", TrialExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/genotype", GenotypeExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/genotype/summary", GenotypeExportSummaryResource.class);
		attachToRouter(routerAuth, "/dataset/export/async", AsyncDatasetExportResource.class);
		attachToRouter(routerAuth, "/dataset/export/async/{jobUuid}", AsyncDatasetExportResource.class);
		attachToRouter(routerUnauth, "/dataset/export/async/{jobUuid}/download", AsyncDatasetExportDownloadResource.class);
		attachToRouter(routerAuth, "/dataset/data/climate/table", ClimateDataTableResource.class);
		attachToRouter(routerAuth, "/dataset/data/climate/table/ids", ClimateDataTableIdResource.class);
		attachToRouter(routerAuth, "/dataset/data/climate/table/export", ClimateDataTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/data/compound/table", CompoundDataTableResource.class);
		attachToRouter(routerAuth, "/dataset/data/compound/table/ids", CompoundDataTableIdResource.class);
		attachToRouter(routerAuth, "/dataset/data/compound/table/export", CompoundDataTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/data/trial/table", TrialsDataTableResource.class);
		attachToRouter(routerAuth, "/dataset/data/trial/table/ids", TrialsDataTableIdResource.class);
		attachToRouter(routerAuth, "/dataset/data/trial/table/export", TrialsDataTableExportResource.class);
		attachToRouter(routerAuth, "/dataset/map", DatasetMapResource.class);
		attachToRouter(routerAuth, "/dataset/stats/climate", ClimateStatsResource.class);
		attachToRouter(routerAuth, "/dataset/stats/compound", CompoundStatsResource.class);
		attachToRouter(routerAuth, "/dataset/stats/trial", TraitStatsResource.class);
		attachToRouter(routerAuth, "/dataset/climate", DatasetClimateResource.class);
		attachToRouter(routerAuth, "/dataset/compound", DatasetCompoundResource.class);
		attachToRouter(routerAuth, "/dataset/trait", DatasetTraitResource.class);
		attachToRouter(routerAuth, "/dataset/group", DatasetGroupResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/user", DatasetUserResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/usergroup", DatasetUsergroupTableResource.class);
		attachToRouter(routerAuth, "/dataset/{datasetId}/usergroup/ids", DatasetUsergroupTableIdResource.class);

		// ENTITIES
		attachToRouter(routerAuth, "/entity/table", EntityTableResource.class);

		// EXPERIMENTS
		attachToRouter(routerAuth, "/experiment/table", ExperimentTableResource.class);

		// GERMPLASM
		attachToRouter(routerAuth, "/germplasm", GermplasmResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/group", GermplasmGroupTableResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/dataset", GermplasmDatasetTableResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/attribute", GermplasmAttributeTableResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/attribute/export", GermplasmAttributeTableExportResource.class);
		attachToRouter(routerAuth, "/germplasm/attribute", GermplasmAttributeTableResource.class);
		attachToRouter(routerAuth, "/germplasm/attribute/export", GermplasmAttributeTableExportResource.class);
		attachToRouter(routerAuth, "/germplasm/distance/table", GermplasmDistanceTableResource.class);
		attachToRouter(routerAuth, "/germplasm/distance/table/ids", GermplasmDistanceTableIdResource.class);
		attachToRouter(routerAuth, "/germplasm/polygon/table", GermplasmPolygonTableResource.class);
		attachToRouter(routerAuth, "/germplasm/polygon/table/ids", GermplasmPolygonTableIdResource.class);
		attachToRouter(routerAuth, "/germplasm/table", GermplasmTableResource.class);
		attachToRouter(routerAuth, "/germplasm/table/ids", GermplasmTableIdResource.class);
		attachToRouter(routerAuth, "/germplasm/table/export", GermplasmTableExportResource.class);
		attachToRouter(routerAuth, "/germplasm/{germplasmId}/mcpd", GermplasmMcpdResource.class);
		attachToRouter(routerAuth, "/germplasm/entity", GermplasmEntityResource.class);
		attachToRouter(routerAuth, "/germplasm/export", GermplasmExportResource.class);

		// GROUPS
		attachToRouter(routerAuth, "/group/table", GroupTableResource.class);
		attachToRouter(routerAuth, "/group", GroupResource.class);
		attachToRouter(routerAuth, "/group/{groupId}", GroupResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm", GroupGermplasmTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm/export", GroupGermplasmTableExportResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm/ids", GroupGermplasmTableIdResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/germplasm/add", GroupGermplasmFileAddResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location", GroupLocationTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location/export", GroupLocationTableExportResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location/ids", GroupLocationTableIdResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/location/add", GroupLocationFileAddResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker", GroupMarkerTableResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker/export", GroupMarkerTableExportResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker/ids", GroupMarkerTableIdResource.class);
		attachToRouter(routerAuth, "/group/{groupId}/marker/add", GroupMarkerFileAddResource.class);
		attachToRouter(routerUnauth, "/group/upload", CurlyWhirlyGroupCreationResource.class);
		attachToRouter(routerAuth, "/grouptype", GroupTypeResource.class);

		// IMAGES
		attachToRouter(routerAuth, "/image/table", ImageTableResource.class);
		attachToRouter(routerAuth, "/image/table/export", ImageTableExportResource.class);
		attachToRouter(routerAuth, "/image/{imageId}/src", ImageSourceResource.class);

		// DATA IMPORT
		attachToRouter(routerAuth, "/import/template/mcpd", McpdImporterResource.class);
		attachToRouter(routerAuth, "/import/template/{uuid}/status", ImportStatusResource.class);

		// INSTITUTIONS
		attachToRouter(routerAuth, "/institution/table", InstitutionTableResource.class);

		// LICENSES
		attachToRouter(routerAuth, "/license/table", LicenseTableResource.class);
		attachToRouter(routerAuth, "/license/{licenseId}/accept", LicenseDecisionResource.class);

		// LOCATIONS
		attachToRouter(routerAuth, "/location/table", LocationTableResource.class);
		attachToRouter(routerAuth, "/location/table/ids", LocationTableIdResource.class);
		attachToRouter(routerAuth, "/location/table/export", LocationTableExportResource.class);
		attachToRouter(routerAuth, "/location/polygon/table", LocationPolygonTableResource.class);
		attachToRouter(routerAuth, "/location/polygon/table/ids", LocationPolygonTableIdResource.class);
		attachToRouter(routerAuth, "/location/distance/table", LocationDistanceTableResource.class);
		attachToRouter(routerAuth, "/location/distance/table/ids", LocationDistanceTableIdResource.class);

		// MAPS
		attachToRouter(routerAuth, "/map/table", MapTableResource.class);
		attachToRouter(routerAuth, "/map", MapResource.class);
		attachToRouter(routerAuth, "/map/{mapId}", MapResource.class);
		attachToRouter(routerAuth, "/map/{mapId}/chromosome", MapChromosomeResource.class);
		attachToRouter(routerAuth, "/map/{mapId}/export", MapExportResource.class);
		attachToRouter(routerAuth, "/map/mapdefinition/table", MapMarkerDefinitionTableResource.class);
		attachToRouter(routerAuth, "/map/mapdefinition/table/ids", MapMarkerDefinitionTableIdResource.class);
		attachToRouter(routerAuth, "/map/mapdefinition/table/export", MapMarkerDefinitionTableExportResource.class);

		// MARKERS
		attachToRouter(routerAuth, "/marker/table", MarkerTableResource.class);
		attachToRouter(routerAuth, "/marker/table/ids", MarkerTableIdResource.class);
		attachToRouter(routerAuth, "/marker/{markerId}/group", MarkerGroupTableResource.class);
		attachToRouter(routerAuth, "/marker/{markerId}/dataset", MarkerDatasetTableResource.class);

		// NEWS
		attachToRouter(routerAuth, "/news/table", NewsTableResource.class);

		// PEDIGREES
		attachToRouter(routerAuth, "/pedigree/table", PedigreeTableResource.class);
		attachToRouter(routerAuth, "/pedigree/table/export", PedigreeTableExportResource.class);
		attachToRouter(routerAuth, "/pedigree/export", PedigreeExportResource.class);

		// TRAITS
		attachToRouter(routerAuth, "/trait/table", TraitTableResource.class);

		// STATS
		attachToRouter(routerAuth, "/stats/biologicalstatus", BiologicalstatusStatsResource.class);
		attachToRouter(routerAuth, "/stats/country", CountryStatsResource.class);
		attachToRouter(routerAuth, "/stats/dataset", DatasetStatsResource.class);
		attachToRouter(routerAuth, "/stats/entitytype", EntityTypeStatsResource.class);
		attachToRouter(routerAuth, "/stats/overview", OverviewStatsResource.class);
		attachToRouter(routerAuth, "/stats/pdci", PdciStatsResource.class);
		attachToRouter(routerAuth, "/stats/taxonomy", TaxonomyStatsResource.class);

		// SETTINGS
		attachToRouter(routerAuth, "/settings/write", SettingsWriterResource.class);
		attachToRouter(routerAuth, "/settings/file", SettingsFileResource.class);

		// USER PERMISSIONS
		attachToRouter(routerAuth, "/usergroup", UsergroupResource.class);
		attachToRouter(routerAuth, "/usergroup/table", UsergroupTableResource.class);
		attachToRouter(routerAuth, "/usergroup/table/ids", UsergroupTableIdResource.class);
		attachToRouter(routerAuth, "/usergroup/{usergroupId}", UsergroupResource.class);
		attachToRouter(routerAuth, "/usergroup/{usergroupId}/user", UserResource.class);
		attachToRouter(routerAuth, "/user", UserResource.class);

		attachToRouter(routerUnauth, "/gatekeeper/institution", GatekeeperInstitutionResource.class);
		attachToRouter(routerUnauth, "/gatekeeper/user/existing", GatekeeperExistingUserResource.class);
		attachToRouter(routerUnauth, "/gatekeeper/user/new", GatekeeperNewUserResource.class);

		// UNAUTH
		attachToRouter(routerUnauth, "/clientlocale", ClientLocaleResource.class);
		attachToRouter(routerUnauth, "/clientlocale/{locale}", ClientLocaleResource.class);
		// TODO: Make this work with AUTH
		attachToRouter(routerUnauth, "/climate/overlay/{overlayId}/src", ClimateOverlaySourceResource.class);
		attachToRouter(routerUnauth, "/image/src", ImageSourceResource.class);
		attachToRouter(routerUnauth, "/image/src-svg/{name}", ImageSvgSourceResource.class); // This is a fix, because <img /> tags don't like SVGs without an extension in their "src" attribute
		attachToRouter(routerUnauth, "/settings", SettingsResource.class);
		attachToRouter(routerUnauth, "/settings/css", SettingsCssResource.class);
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

	public static String getServerBase(HttpServletRequest req)
	{
		String scheme = req.getScheme(); // http or https
		String serverName = req.getServerName(); // ics.hutton.ac.uk
		int serverPort = req.getServerPort(); // 80 or 8080 or 443
		String contextPath = req.getContextPath(); // /germinate-baz

		if (serverPort == 80 || serverPort == 443)
			return scheme + "://" + serverName + contextPath; // http://ics.hutton.ac.uk/germinate-baz
		else
			return scheme + "://" + serverName + ":" + serverPort + contextPath; // http://ics.hutton.ac.uk:8080/germinate-baz
	}
}
