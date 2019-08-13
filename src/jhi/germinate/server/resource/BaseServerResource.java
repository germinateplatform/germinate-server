package jhi.germinate.server.resource;

import org.restlet.resource.*;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class BaseServerResource extends ServerResource
{
	protected File createTempFile(String filename, String extension)
		throws IOException
	{
		extension = extension.replace(".", "");

		List<String> segments = getReference().getSegments(true);
		String path;
		if (segments == null || segments.size() < 1)
			path = "germinate";
		else
			path = segments.get(0);
		File folder = new File(System.getProperty("java.io.tmpdir"), path);
		folder.mkdirs();

		File file;
		do
		{
			file = new File(folder, filename + "-" + UUID.randomUUID() + "." + extension);
		} while (file.exists());

		file.createNewFile();

		return file;
	}
}
