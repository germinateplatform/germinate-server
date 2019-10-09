package jhi.germinate.server.resource;

import org.jooq.*;
import org.restlet.resource.ServerResource;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class BaseServerResource extends ServerResource
{
	protected static final String CRLF = "\r\n";

	protected static void exportToFile(PrintWriter bw, Result<? extends Record> results, boolean includeHeaders)
	{
		Row row = results.fieldsRow();
		if (includeHeaders)
		{
			bw.print(row.fieldStream()
						.map(Field::getName)
						.collect(Collectors.joining("\t", "", CRLF)));
		}
		results.forEach(r -> bw.print(IntStream.range(0, row.size())
											   .boxed()
											   .map(i -> {
												   Object value = r.getValue(i);
												   if (value == null)
													   return "";
												   else
													   return value.toString();
											   })
											   .collect(Collectors.joining("\t", "", CRLF))));
	}

	protected File getLibFolder()
		throws URISyntaxException
	{
		URL resource = PropertyWatcher.class.getClassLoader().getResource("logging.properties");
		if (resource != null)
		{
			File file = new File(resource.toURI());
			return new File(file.getParentFile().getParentFile(), "lib");
		}

		return null;
	}

	protected File getFromExternal(String filename, String... subdirs)
	{
		File folder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL));

		if (subdirs != null)
		{
			for (int i = 0; i < subdirs.length; i++)
			{
				folder = new File(folder, subdirs[i]);
			}
		}

		return new File(folder, filename);
	}

	protected File getTempDir(String parentFolder)
	{
		List<String> segments = getReference().getSegments(true);
		String path;
		if (CollectionUtils.isEmpty(segments))
			path = "germinate";
		else
			path = segments.get(0);
		File folder = new File(System.getProperty("java.io.tmpdir"), path);

		return new File(folder, parentFolder);
	}

	protected File createTempFile(String parentFolder, String filename, String extension)
		throws IOException
	{
		extension = extension.replace(".", "");

		List<String> segments = getReference().getSegments(true);

		String path;
		if (CollectionUtils.isEmpty(segments))
			path = "germinate";
		else
			path = segments.get(0);
		File folder = new File(System.getProperty("java.io.tmpdir"), path);
		folder.mkdirs();

		if (!StringUtils.isEmpty(parentFolder))
		{
			folder = new File(folder, parentFolder);
			folder.mkdirs();
		}

		File file;
		do
		{
			file = new File(folder, filename + "-" + UUID.randomUUID() + "." + extension);
		} while (file.exists());

		file.createNewFile();

		return file;
	}

	protected File createTempFile(String filename, String extension)
		throws IOException
	{
		return createTempFile(null, filename, extension);
	}
}
