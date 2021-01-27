package jhi.germinate.server.util;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

public class FileUtils
{
	/**
	 * Creates a zip file in place of the given file and adds all files from the list to it.
	 * @param zipFile The zip file to create
	 * @param files The files to add to the zip file
	 */
	public static void zipUp(File zipFile, List<File> files)
	{
		// Make sure it doesn't exist
		if (zipFile.exists())
			zipFile.delete();

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);
		URI uri = URI.create("jar:file:/" + prefix);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

		try (FileSystem fs = FileSystems.newFileSystem(uri, env, null))
		{
			for (File f : files)
			{
				Files.copy(f.toPath(), fs.getPath("/" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
				f.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
