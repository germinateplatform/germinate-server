package jhi.germinate.server.resource.settings;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Path("settings")
public class SettingsResource
{
	public static Set<String> AUTO_DISCOVERY_HIDDEN_PAGES = null;

	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSettings()
	{
		AuthenticationMode authMode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		boolean dbConValid = Database.check(PropertyWatcher.get(ServerProperty.DATABASE_SERVER), PropertyWatcher.get(ServerProperty.DATABASE_NAME), PropertyWatcher.get(ServerProperty.DATABASE_PORT), PropertyWatcher.get(ServerProperty.DATABASE_USERNAME), PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD));

		// If something isn't configured correctly, then the client needs to run through setup
		if (!dbConValid || (authMode != AuthenticationMode.NONE && !GatekeeperClient.connectionValid()))
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		else
		{
			List<String> hiddenPages = new ArrayList<>();

			// Add any auto-discovered pages
			if (AUTO_DISCOVERY_HIDDEN_PAGES != null)
				hiddenPages = new ArrayList<>(AUTO_DISCOVERY_HIDDEN_PAGES);

			// Then add any that the user specifically requested
			hiddenPages.addAll(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_PAGES, String.class));

			return Response.ok(new ClientConfiguration()
					.setColorsCharts(PropertyWatcher.getPropertyList(ServerProperty.COLORS_CHART, String.class))
					.setColorsTemplate(PropertyWatcher.getPropertyList(ServerProperty.COLORS_TEMPLATE, String.class))
					.setColorsGradient(PropertyWatcher.getPropertyList(ServerProperty.COLORS_GRADIENT, String.class))
					.setColorPrimary(PropertyWatcher.get(ServerProperty.COLOR_PRIMARY))
					.setDashboardCategories(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_CATEGORIES, String.class))
					.setDashboardSections(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_SECTIONS, String.class))
					.setHiddenPages(hiddenPages)
					.setAuthMode(PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class))
					.setRegistrationEnabled(PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
					.setExternalLinkIdentifier(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER))
					.setExternalLinkTemplate(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_TEMPLATE))
					.setShowGdprNotification(PropertyWatcher.getBoolean(ServerProperty.GRPD_NOTIFICATION_ENABLED))
					.setGoogleAnalyticsKey(PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_KEY))
					.setGenesysUrl(PropertyWatcher.get(ServerProperty.GENESYS_URL))
					.setHiddenColumns(new HiddenColumns()
							.setGermplasm(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM, String.class))
							.setGermplasmAttributes(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM_ATTRIBUTES, String.class))
							.setImages(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_IMAGES, String.class))
							.setClimates(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATES, String.class))
							.setClimateData(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATE_DATA, String.class))
							.setComments(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_COMMENTS, String.class))
							.setFileresources(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_FILERESOURCES, String.class))
							.setMaps(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MAPS, String.class))
							.setMarkers(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MARKERS, String.class))
							.setMapDefinitions(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MAP_DEFIITIONS, String.class))
							.setDatasets(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASETS, String.class))
							.setDatasetAttributes(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASET_ATTRIBUTES, String.class))
							.setExperiments(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_EXPERIMENTS, String.class))
							.setEntities(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_ENTITIES, String.class))
							.setGroups(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GROUPS, String.class))
							.setInstitutions(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_INSTITUTIONS, String.class))
							.setLocations(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_LOCATIONS, String.class))
							.setPedigrees(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_PEDIGREES, String.class))
							.setTraits(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_TRAITS, String.class))
							.setTrialsData(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_TRIALS_DATA, String.class))
							.setCollaborators(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_COLLABORATORS, String.class))
							.setPublications(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_PUBLICATIONS, String.class))
					)
					.setPlausibleApiHost(PropertyWatcher.get(ServerProperty.PLAUSIBLE_API_HOST))
					.setPlausibleHashMode(PropertyWatcher.getBoolean(ServerProperty.PLAUSIBLE_HASH_MODE))
					.setPlausibleDomain(PropertyWatcher.get(ServerProperty.PLAUSIBLE_DOMAIN))
					.setGatekeeperUrl(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL))
					.setCommentsEnabled(PropertyWatcher.getBoolean(ServerProperty.COMMENTS_ENABLED))
					.setDataImportMode(PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class))
					.setGridscoreUrl(PropertyWatcher.get(ServerProperty.GRIDSCORE_URL))
					.setHeliumUrl(PropertyWatcher.get(ServerProperty.HELIUM_URL))
					.setFieldhubUrl(PropertyWatcher.get(ServerProperty.FIELDHUB_URL))
					.setSupportsFeedback(!StringUtils.isEmpty(PropertyWatcher.get(ServerProperty.FEEDBACK_EMAIL)) && PropertyWatcher.isEmailConfigured())
			).build();
		}
	}

	@GET
	@Path("/admin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public ClientAdminConfiguration getAdminSettings()
	{
		// Get the admin specific settings
		ClientAdminConfiguration result = new ClientAdminConfiguration()
				.setBcryptSalt(PropertyWatcher.getInteger(ServerProperty.BCRYPT_SALT))
				.setBrapiEnabled(PropertyWatcher.getBoolean(ServerProperty.BRAPI_ENABLED))
				.setDataDirectoryExternal(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL))
				.setFilesDeleteAfterHoursAsync(PropertyWatcher.getInteger(ServerProperty.FILES_DELETE_AFTER_HOURS_ASYNC))
				.setFilesDeleteAfterHoursTemp(PropertyWatcher.getInteger(ServerProperty.FILES_DELETE_AFTER_HOURS_TEMP))
				.setGatekeeperUsername(PropertyWatcher.get(ServerProperty.GATEKEEPER_USERNAME))
				.setGatekeeperPassword(PropertyWatcher.get(ServerProperty.GATEKEEPER_PASSWORD))
				.setGatekeeperRegistrationRequiresApproval(PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL))
				.setPdciEnabled(PropertyWatcher.getBoolean(ServerProperty.PDCI_ENABLED))
				.setHiddenPagesAutodiscover(PropertyWatcher.getBoolean(ServerProperty.HIDDEN_PAGES_AUTODISCOVER));

		// Get all the base settings as well
		result.setColorsCharts(PropertyWatcher.getPropertyList(ServerProperty.COLORS_CHART, String.class))
			  .setColorsTemplate(PropertyWatcher.getPropertyList(ServerProperty.COLORS_TEMPLATE, String.class))
			  .setColorsGradient(PropertyWatcher.getPropertyList(ServerProperty.COLORS_GRADIENT, String.class))
			  .setColorPrimary(PropertyWatcher.get(ServerProperty.COLOR_PRIMARY))
			  .setDashboardCategories(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_CATEGORIES, String.class))
			  .setDashboardSections(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_SECTIONS, String.class))
			  .setHiddenPages(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_PAGES, String.class))
			  .setAuthMode(PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class))
			  .setRegistrationEnabled(PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
			  .setExternalLinkIdentifier(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER))
			  .setExternalLinkTemplate(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_TEMPLATE))
			  .setShowGdprNotification(PropertyWatcher.getBoolean(ServerProperty.GRPD_NOTIFICATION_ENABLED))
			  .setGoogleAnalyticsKey(PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_KEY))
			  .setHiddenColumns(new HiddenColumns()
					  .setGermplasm(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM, String.class))
					  .setGermplasmAttributes(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM_ATTRIBUTES, String.class))
					  .setImages(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_IMAGES, String.class))
					  .setClimates(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATES, String.class))
					  .setClimateData(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATE_DATA, String.class))
					  .setComments(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_COMMENTS, String.class))
					  .setFileresources(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_FILERESOURCES, String.class))
					  .setMaps(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MAPS, String.class))
					  .setMarkers(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MARKERS, String.class))
					  .setMapDefinitions(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_MAP_DEFIITIONS, String.class))
					  .setDatasets(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASETS, String.class))
					  .setDatasetAttributes(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASET_ATTRIBUTES, String.class))
					  .setExperiments(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_EXPERIMENTS, String.class))
					  .setEntities(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_ENTITIES, String.class))
					  .setGroups(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_GROUPS, String.class))
					  .setInstitutions(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_INSTITUTIONS, String.class))
					  .setLocations(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_LOCATIONS, String.class))
					  .setPedigrees(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_PEDIGREES, String.class))
					  .setTraits(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_TRAITS, String.class))
					  .setTrialsData(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_TRIALS_DATA, String.class))
					  .setCollaborators(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_COLLABORATORS, String.class))
					  .setPublications(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_COLUMNS_PUBLICATIONS, String.class))
			  )
			  .setPlausibleApiHost(PropertyWatcher.get(ServerProperty.PLAUSIBLE_API_HOST))
			  .setPlausibleHashMode(PropertyWatcher.getBoolean(ServerProperty.PLAUSIBLE_HASH_MODE))
			  .setPlausibleDomain(PropertyWatcher.get(ServerProperty.PLAUSIBLE_DOMAIN))
			  .setGatekeeperUrl(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL))
			  .setCommentsEnabled(PropertyWatcher.getBoolean(ServerProperty.COMMENTS_ENABLED))
			  .setDataImportMode(PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class))
			  .setGridscoreUrl(PropertyWatcher.get(ServerProperty.GRIDSCORE_URL))
			  .setHeliumUrl(PropertyWatcher.get(ServerProperty.HELIUM_URL))
			  .setFieldhubUrl(PropertyWatcher.get(ServerProperty.FIELDHUB_URL))
			  .setSupportsFeedback(!StringUtils.isEmpty(PropertyWatcher.get(ServerProperty.FEEDBACK_EMAIL)) && PropertyWatcher.isEmailConfigured());

		return result;
	}

	@POST
	@Path("/admin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean postAdminSettings(ClientAdminConfiguration config)
			throws IOException
	{
		// This is the only one that's really required for Germinate to run. Everything else is optional.
		// This doesn't mean that sending properties without other settings won't screw things over. If no Gatekeeper settings are returned,
		// then logging back into Germinate to change the settings again won't be possible anymore.
		if (StringUtils.isEmpty(config.getDataDirectoryExternal()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		PropertyWatcher.setInteger(ServerProperty.BRAPI_ENABLED, config.getBcryptSalt());
		PropertyWatcher.setBoolean(ServerProperty.BRAPI_ENABLED, config.getBrapiEnabled());
		PropertyWatcher.set(ServerProperty.DATA_DIRECTORY_EXTERNAL, config.getDataDirectoryExternal());
		PropertyWatcher.setInteger(ServerProperty.FILES_DELETE_AFTER_HOURS_ASYNC, config.getFilesDeleteAfterHoursAsync());
		PropertyWatcher.setInteger(ServerProperty.FILES_DELETE_AFTER_HOURS_TEMP, config.getFilesDeleteAfterHoursTemp());
		PropertyWatcher.set(ServerProperty.GATEKEEPER_USERNAME, config.getGatekeeperUsername());
		PropertyWatcher.set(ServerProperty.GATEKEEPER_PASSWORD, config.getGatekeeperPassword());
		PropertyWatcher.setBoolean(ServerProperty.GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL, config.getGatekeeperRegistrationRequiresApproval());
		PropertyWatcher.setBoolean(ServerProperty.PDCI_ENABLED, config.getPdciEnabled());
		PropertyWatcher.setPropertyList(ServerProperty.COLORS_CHART, config.getColorsCharts());
		PropertyWatcher.setPropertyList(ServerProperty.COLORS_TEMPLATE, config.getColorsTemplate());
		PropertyWatcher.setPropertyList(ServerProperty.COLORS_GRADIENT, config.getColorsGradient());
		PropertyWatcher.set(ServerProperty.COLOR_PRIMARY, config.getColorPrimary());
		PropertyWatcher.setPropertyList(ServerProperty.DASHBOARD_CATEGORIES, config.getDashboardCategories());
		PropertyWatcher.setPropertyList(ServerProperty.DASHBOARD_SECTIONS, config.getDashboardSections());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_PAGES, config.getHiddenPages());
		PropertyWatcher.setBoolean(ServerProperty.HIDDEN_PAGES_AUTODISCOVER, config.getHiddenPagesAutodiscover());
		PropertyWatcher.set(ServerProperty.AUTHENTICATION_MODE, config.getAuthMode().name());
		PropertyWatcher.setBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED, config.getRegistrationEnabled());
		PropertyWatcher.set(ServerProperty.EXTERNAL_LINK_IDENTIFIER, config.getExternalLinkIdentifier());
		PropertyWatcher.set(ServerProperty.EXTERNAL_LINK_TEMPLATE, config.getExternalLinkTemplate());
		PropertyWatcher.setBoolean(ServerProperty.GRPD_NOTIFICATION_ENABLED, config.getShowGdprNotification());
		PropertyWatcher.set(ServerProperty.GOOGLE_ANALYTICS_KEY, config.getGoogleAnalyticsKey());
		PropertyWatcher.set(ServerProperty.PLAUSIBLE_DOMAIN, config.getPlausibleDomain());
		PropertyWatcher.setBoolean(ServerProperty.PLAUSIBLE_HASH_MODE, config.getPlausibleHashMode());
		PropertyWatcher.set(ServerProperty.PLAUSIBLE_API_HOST, config.getPlausibleApiHost());
		PropertyWatcher.set(ServerProperty.GATEKEEPER_URL, config.getGatekeeperUrl());
		PropertyWatcher.setBoolean(ServerProperty.COMMENTS_ENABLED, config.getCommentsEnabled());
		PropertyWatcher.set(ServerProperty.DATA_IMPORT_MODE, config.getDataImportMode().name());
		PropertyWatcher.set(ServerProperty.GRIDSCORE_URL, config.getGridscoreUrl());
		PropertyWatcher.set(ServerProperty.HELIUM_URL, config.getHeliumUrl());
		PropertyWatcher.set(ServerProperty.FIELDHUB_URL, config.getFieldhubUrl());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM, config.getHiddenColumns().getGermplasm());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_GERMPLASM_ATTRIBUTES, config.getHiddenColumns().getGermplasmAttributes());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_IMAGES, config.getHiddenColumns().getImages());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATES, config.getHiddenColumns().getClimates());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_CLIMATE_DATA, config.getHiddenColumns().getClimateData());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_COMMENTS, config.getHiddenColumns().getComments());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_FILERESOURCES, config.getHiddenColumns().getFileresources());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_MAPS, config.getHiddenColumns().getMaps());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_MARKERS, config.getHiddenColumns().getMarkers());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_MAP_DEFIITIONS, config.getHiddenColumns().getMapDefinitions());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASETS, config.getHiddenColumns().getDatasets());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_DATASET_ATTRIBUTES, config.getHiddenColumns().getDatasetAttributes());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_EXPERIMENTS, config.getHiddenColumns().getExperiments());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_ENTITIES, config.getHiddenColumns().getEntities());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_GROUPS, config.getHiddenColumns().getGroups());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_INSTITUTIONS, config.getHiddenColumns().getInstitutions());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_LOCATIONS, config.getHiddenColumns().getLocations());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_PEDIGREES, config.getHiddenColumns().getPedigrees());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_TRAITS, config.getHiddenColumns().getTraits());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_TRIALS_DATA, config.getHiddenColumns().getTrialsData());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_COLLABORATORS, config.getHiddenColumns().getCollaborators());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_COLUMNS_PUBLICATIONS, config.getHiddenColumns().getPublications());

		// Invalidate all tokens
		AuthenticationFilter.invalidateAllTokens();

		return PropertyWatcher.storeProperties();
	}

	@POST
	@Path("/carousel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean postTemplateCarouselConfig(CarouselConfig config)
			throws IOException
	{
		Gson gson = new Gson();
		Type type = new TypeToken<CarouselConfig>()
		{
		}.getType();

		// Read the carousel.json file
		File configFile = ResourceUtils.getFromExternal(resp, "carousel.json", "template");
		configFile.getParentFile().mkdirs();

		// Write the file back
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
		{
			gson.toJson(config, type, writer);
		}

		return true;
	}

	@GET
	@Path("/carousel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCarouselConfig()
			throws IOException
	{
		File configFile = ResourceUtils.getFromExternal(resp, "carousel.json", "template");
		Gson gson = new Gson();
		Type type = new TypeToken<CarouselConfig>()
		{
		}.getType();

		if (configFile == null || !configFile.exists())
		{
			return Response.ok(Response.Status.NOT_FOUND).build();
		}
		else
		{
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				return Response.ok(gson.fromJson(br, type)).build();
			}
		}
	}

	@POST
	@Path("/about")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public Response postTemplateAboutConfig(@FormDataParam("name") String name, @FormDataParam("description") String description, @FormDataParam("group") String group, @FormDataParam("url") String url, @FormDataParam("imageFile") InputStream fileIs, @FormDataParam("imageFile") FormDataContentDisposition fileDetails)
			throws IOException
	{
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(url) || fileIs == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		AboutConfig.AboutInfo newInfo = new AboutConfig.AboutInfo()
				.setName(name)
				.setDescription(description)
				.setGroup(group)
				.setUrl(url);

		// Get the image template folder
		File folder = new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.template.name());
		folder.mkdirs();

		// Extract extension from original file
		String itemName = fileDetails.getFileName();
		String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
		// Create random uuid filename
		String uuid = UUID.randomUUID().toString();
		// Concatenate target
		File targetFile = new File(folder, uuid + "." + extension);

		if (!FileUtils.isSubDirectory(folder, targetFile))
			return Response.status(Response.Status.BAD_REQUEST).build();

		Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		// Store the name
		newInfo.setImage(targetFile.getName());

		Gson gson = new Gson();
		Type type = new TypeToken<AboutConfig>()
		{
		}.getType();

		// Read the carousel.json file
		File configFile = ResourceUtils.getFromExternal(resp, "about.json", "template");

		if (configFile == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		configFile.getParentFile().mkdirs();

		if (!configFile.exists())
		{
			// Write the file back
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
			{
				AboutConfig c = new AboutConfig();
				c.add(newInfo);
				gson.toJson(c, type, writer);
			}
		}
		else
		{
			AboutConfig c;
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				c = gson.fromJson(br, type);
				c.add(newInfo);
			}

			// Write the file back
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
			{
				gson.toJson(c, type, writer);
			}
		}

		return Response.ok(true).build();
	}

	@PATCH
	@Path("/about")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public boolean patchTemplateAboutConfig(AboutConfig config)
			throws IOException
	{
		Gson gson = new Gson();
		Type type = new TypeToken<AboutConfig>()
		{
		}.getType();

		// Read the carousel.json file
		File configFile = ResourceUtils.getFromExternal(resp, "about.json", "template");
		configFile.getParentFile().mkdirs();

		// Write the file back
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
		{
			gson.toJson(config, type, writer);
		}

		return true;
	}

	@GET
	@Path("/about")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AboutConfig getAboutConfig()
			throws IOException
	{
		File configFile = ResourceUtils.getFromExternal(resp, "about.json", "template");
		Gson gson = new Gson();
		Type type = new TypeToken<AboutConfig>()
		{
		}.getType();

		if (configFile == null || !configFile.exists())
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}
		else
		{
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				return gson.fromJson(br, type);
			}
		}
	}

	@PATCH
	@Path("/template/i18n/{lang}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public Response patchTemplateI18n(@PathParam("lang") String lang, Map<String, String> i18n)
			throws IOException
	{
		if (StringUtils.isEmpty(lang) || i18n == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		File langFile = ResourceUtils.getFromExternal(resp, lang + ".json", "template");

		if (langFile == null || !langFile.exists())
			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String, String> content = null;

		// Read the file
		try (Reader br = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8))
		{
			content = gson.fromJson(br, new TypeToken<Map<String, String>>()
			{
			}.getType());
		}

		// Write the new content
		final Map<String, String> finalMap = content;
		if (content != null)
		{
			i18n.forEach((key, value) -> finalMap.put(key, value));

			// Write the file
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(langFile), StandardCharsets.UTF_8))
			{
				gson.toJson(finalMap, writer);
			}
		}

		return Response.ok().build();
	}

	@GET
	@Path("/css")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/css")
	public Response getSettingsCss()
	{
		String primaryColor = PropertyWatcher.get(ServerProperty.COLOR_PRIMARY);

		if (StringUtils.isEmpty(primaryColor))
			return null;

		File cssFile = ResourceUtils.getTempDir(primaryColor + ".css");

		if (!cssFile.exists())
		{
			boolean worked = createCssFile(primaryColor, cssFile);

			if (!worked)
				return null;
		}

		return Response.ok(cssFile)
					   .type("text/css")
					   .header("content-disposition", "attachment;filename= \"" + cssFile.getName() + "\"")
					   .header("content-length", cssFile.length())
					   .build();
	}

	private boolean createCssFile(String color, File targetFile)
	{
		try
		{
			Color primary = Color.fromHex(color);
			Color primary5 = primary.toTransparency(0.5f);
			Color primary25 = primary.toTransparency(0.25f);
			Color darker = primary.darker();
			Color hover = darker.darker();
			Color darkerShadow = darker.darker().toTransparency(0.5f);
			Color lighterBorder = primary.brighter().brighter();

			File template = new File(SettingsResource.class.getClassLoader().getResource("template.css").toURI());

			String content = Files.readString(template.toPath());
			content = content.replace("{{PRIMARY}}", primary.toHexValue())
							 .replace("{{PRIMARY_HOVER}}", hover.toHexValue())
							 .replace("{{PRIMARY_DARKER}}", darker.toHexValue())
							 .replace("{{PRIMARY_LIGHTER_BORDER}}", lighterBorder.toHexValue())
							 .replace("{{PRIMARY_DARKER_SHADOW}}", darkerShadow.toHexValue())
							 .replace("{{PRIMARY_SHADOW}}", primary5.toHexValue())
							 .replace("{{PRIMARY_LIGHTER_SHADOW}}", primary25.toHexValue());
			Files.write(targetFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

			return true;
		}
		catch (NullPointerException | URISyntaxException | IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private Response getFile(String name)
			throws GerminateException, IOException
	{
		try
		{
			File file = ResourceUtils.getFromExternal(resp, name, "template");

			if (file != null && file.exists() && file.isFile())
			{
				return Response.ok(file)
							   .type("text/plain")
							   .header("content-disposition", "attachment;filename= \"" + file.getName() + "\"")
							   .header("content-length", file.length())
							   .build();
			}
			else
			{
				throw new GerminateException(Response.Status.NOT_FOUND);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}
