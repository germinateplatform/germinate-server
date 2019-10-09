package jhi.germinate.server.resource.settings;

import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class SettingsCssResource extends BaseServerResource
{
	@Get("text/css")
	public FileRepresentation getJson()
	{
		String primaryColor = PropertyWatcher.get(ServerProperty.COLOR_PRIMARY);

		if (StringUtils.isEmpty(primaryColor))
			return null;

		File cssFile = getTempDir(primaryColor + ".css");

		if (!cssFile.exists())
		{
			boolean worked = createCssFile(primaryColor, cssFile);

			if (!worked)
				return null;
		}

		FileRepresentation representation = new FileRepresentation(cssFile, MediaType.TEXT_CSS);
		representation.setSize(cssFile.length());
		representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
		return representation;
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

			File template = new File(SettingsCssResource.class.getClassLoader().getResource("template.css").toURI());

			String content = new String(Files.readAllBytes(template.toPath()), StandardCharsets.UTF_8);
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
}
