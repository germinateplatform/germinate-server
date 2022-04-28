package jhi.germinate.server.resource.settings;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Path("settings")
public class SettingsResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientConfiguration getSettings()
	{
		return new ClientConfiguration()
			.setColorsCharts(PropertyWatcher.getPropertyList(ServerProperty.COLORS_CHART, String.class))
			.setColorsTemplate(PropertyWatcher.getPropertyList(ServerProperty.COLORS_TEMPLATE, String.class))
			.setColorPrimary(PropertyWatcher.get(ServerProperty.COLOR_PRIMARY))
			.setDashboardCategories(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_CATEGORIES, String.class))
			.setHiddenPages(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_PAGES, String.class))
			.setAuthMode(PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class))
			.setRegistrationEnabled(PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
			.setExternalLinkIdentifier(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER))
			.setExternalLinkTemplate(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_TEMPLATE))
			.setShowGdprNotification(PropertyWatcher.getBoolean(ServerProperty.GRPD_NOTIFICATION_ENABLED))
			.setGoogleAnalyticsKey(PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_KEY))
			.setPlausibleApiHost(PropertyWatcher.get(ServerProperty.PLAUSIBLE_API_HOST))
			.setPlausibleHashMode(PropertyWatcher.getBoolean(ServerProperty.PLAUSIBLE_HASH_MODE))
			.setPlausibleDomain(PropertyWatcher.get(ServerProperty.PLAUSIBLE_DOMAIN))
			.setGatekeeperUrl(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL))
			.setCommentsEnabled(PropertyWatcher.getBoolean(ServerProperty.COMMENTS_ENABLED))
			.setDataImportMode(PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class))
			.setHeliumUrl(PropertyWatcher.get(ServerProperty.HELIUM_URL));
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
			.setPdciEnabled(PropertyWatcher.getBoolean(ServerProperty.PDCI_ENABLED));

		// Get all the base settings as well
		result.setColorsCharts(PropertyWatcher.getPropertyList(ServerProperty.COLORS_CHART, String.class))
			  .setColorsTemplate(PropertyWatcher.getPropertyList(ServerProperty.COLORS_TEMPLATE, String.class))
			  .setColorPrimary(PropertyWatcher.get(ServerProperty.COLOR_PRIMARY))
			  .setDashboardCategories(PropertyWatcher.getPropertyList(ServerProperty.DASHBOARD_CATEGORIES, String.class))
			  .setHiddenPages(PropertyWatcher.getPropertyList(ServerProperty.HIDDEN_PAGES, String.class))
			  .setAuthMode(PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class))
			  .setRegistrationEnabled(PropertyWatcher.getBoolean(ServerProperty.GATEKEEPER_REGISTRATION_ENABLED))
			  .setExternalLinkIdentifier(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER))
			  .setExternalLinkTemplate(PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_TEMPLATE))
			  .setShowGdprNotification(PropertyWatcher.getBoolean(ServerProperty.GRPD_NOTIFICATION_ENABLED))
			  .setGoogleAnalyticsKey(PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_KEY))
			  .setPlausibleApiHost(PropertyWatcher.get(ServerProperty.PLAUSIBLE_API_HOST))
			  .setPlausibleHashMode(PropertyWatcher.getBoolean(ServerProperty.PLAUSIBLE_HASH_MODE))
			  .setPlausibleDomain(PropertyWatcher.get(ServerProperty.PLAUSIBLE_DOMAIN))
			  .setGatekeeperUrl(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL))
			  .setCommentsEnabled(PropertyWatcher.getBoolean(ServerProperty.COMMENTS_ENABLED))
			  .setDataImportMode(PropertyWatcher.get(ServerProperty.DATA_IMPORT_MODE, DataImportMode.class))
			  .setHeliumUrl(PropertyWatcher.get(ServerProperty.HELIUM_URL));

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
		PropertyWatcher.set(ServerProperty.COLOR_PRIMARY, config.getColorPrimary());
		PropertyWatcher.setPropertyList(ServerProperty.DASHBOARD_CATEGORIES, config.getDashboardCategories());
		PropertyWatcher.setPropertyList(ServerProperty.HIDDEN_PAGES, config.getHiddenPages());
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
		PropertyWatcher.set(ServerProperty.HELIUM_URL, config.getHeliumUrl());

		// Invalidate all tokens
		AuthenticationFilter.invalidateAllTokens();

		return PropertyWatcher.storeProperties();
	}

	@GET
	@Path("/file")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSettingsFile(@QueryParam("file-type") String type)
		throws IOException
	{
		try
		{
			switch (type)
			{
				case "carousel":
					return getFile("carousel.json");
				default:
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return null;
			}
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
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

			if (file.exists() && file.isFile())
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
