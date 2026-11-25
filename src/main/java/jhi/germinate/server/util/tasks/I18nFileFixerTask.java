package jhi.germinate.server.util.tasks;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jhi.germinate.resource.LocaleConfig;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 * Since vue-i18n introduced a breaking change in v9 regarding special characters like `@`, it's necessary to transform
 * any locale files to make sure they don't contain any unescaped versions of these characters.
 */
public class I18nFileFixerTask implements Runnable
{
	@Override
	public void run()
	{
		File templateFolder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "template");

		File configFile = new File(templateFolder, "locales.json");
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<LocaleConfig>>()
		{
		}.getType();

		if (configFile.exists())
		{
			try (Reader br = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
			{
				List<LocaleConfig> configs = gson.fromJson(br, type);

				for (LocaleConfig config : configs)
				{
					File jsonFile = new File(templateFolder, config.getLocale() + ".json");

					if (jsonFile.exists() && jsonFile.isFile())
						new I18nFileFixer().migrateFileInPlace(jsonFile.toPath());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class I18nFileFixer
	{
		// Matches existing vue-i18n interpolations/literals like {name}, {0}, {'@'}, {'foo bar'}
		private static final Pattern INTERPOLATION_OR_LITERAL =
				Pattern.compile("\\{'[^']*'\\}|\\{[a-zA-Z0-9_$-]+\\}");

		private final Gson gson;

		public I18nFileFixer()
		{
			this.gson = new GsonBuilder()
					.setPrettyPrinting()
					// Avoid escaping characters like '=' or '<' if not needed
					.disableHtmlEscaping()
					.create();
		}

		public void migrateFileInPlace(Path filePath)
				throws IOException
		{
			Logger.getLogger("").info("I18nFileFixerTask processing file: " + filePath.getFileName().toString());

			JsonElement root;
			try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
			{
				root = JsonParser.parseReader(reader);
			}

			JsonElement migrated = transformElement(root);

			Path tempFile = Files.createTempFile(
					filePath.toAbsolutePath().getParent(),
					".i18n-migrate-",
					".tmp"
			);
			try
			{
				try (Writer writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8))
				{
					gson.toJson(migrated, writer);
				}
				Files.move(
						tempFile,
						filePath,
						StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.ATOMIC_MOVE
				);
			}
			finally
			{
				Files.deleteIfExists(tempFile);
			}
		}

		private JsonElement transformElement(JsonElement element)
		{
			if (element == null || element.isJsonNull())
			{
				return element;
			}
			if (element.isJsonPrimitive())
			{
				JsonPrimitive primitive = element.getAsJsonPrimitive();
				if (primitive.isString())
				{
					return new JsonPrimitive(migrateMessage(primitive.getAsString()));
				}
				return primitive;
			}
			if (element.isJsonArray())
			{
				JsonArray array = element.getAsJsonArray();
				JsonArray updated = new JsonArray(array.size());
				for (JsonElement item : array)
				{
					updated.add(transformElement(item));
				}
				return updated;
			}
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();
				JsonObject updated = new JsonObject();
				for (Map.Entry<String, JsonElement> entry : obj.entrySet())
				{
					updated.add(entry.getKey(), transformElement(entry.getValue()));
				}
				return updated;
			}
			return element;
		}

		String migrateMessage(String message)
		{
			if (message == null || message.isEmpty())
			{
				return message;
			}

			StringBuilder protectedText = new StringBuilder(message.length());
			int cursor = 0;
			int tokenIndex = 0;
			List<String> preservedTokens = new ArrayList<>();

			while (cursor < message.length())
			{
				Matcher interpMatcher = INTERPOLATION_OR_LITERAL.matcher(message).region(cursor, message.length());

				if (interpMatcher.lookingAt())
				{
					String match = interpMatcher.group();
					String sentinel = "__VUE_I18N_TOKEN_" + (tokenIndex++) + "__";
					preservedTokens.add(match);
					protectedText.append(sentinel);
					cursor += match.length();
				}
				else
				{
					protectedText.append(message.charAt(cursor));
					cursor++;
				}
			}

			String result = protectedText.toString().replace("@", "{'@'}");

			for (int i = 0; i < preservedTokens.size(); i++)
			{
				result = result.replace("__VUE_I18N_TOKEN_" + i + "__", preservedTokens.get(i));
			}

			return result;
		}
	}
}
