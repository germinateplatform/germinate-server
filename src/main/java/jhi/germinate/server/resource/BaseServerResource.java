package jhi.germinate.server.resource;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.*;
import org.restlet.resource.ServerResource;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Sebastian Raubach
 */
public class BaseServerResource extends ServerResource
{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	protected static final String CRLF = "\r\n";

	protected static void exportToFile(Writer bw, Result<? extends Record> results, boolean includeHeaders, PaginatedServerResource.ExportSettings settings)
		throws IOException
	{
		List<Field> columnsToNullList = new ArrayList<>();
		if (settings != null && settings.fieldsToNull != null)
		{
			columnsToNullList.addAll(Arrays.asList(settings.fieldsToNull));
		}
		Row row = results.fieldsRow();
		if (includeHeaders)
		{
			bw.write(row.fieldStream()
						.filter(f -> !columnsToNullList.contains(f))
						.map(Field::getName)
						.collect(Collectors.joining("\t", "", CRLF)));
		}
		results.forEach(r -> {
			try
			{
				bw.write(IntStream.range(0, row.size())
								  .boxed()
								  .filter(i -> !columnsToNullList.contains(row.field(i)))
								  .map(i -> {
									  Object value = r.getValue(i);
									  if (value == null)
										  return "";
									  else
										  return value.toString();
								  })
								  .collect(Collectors.joining("\t", "", CRLF)));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		});
	}

	public static File getLibFolder()
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

	public static File getFromExternal(String filename, String... subdirs)
	{
		File folder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL));

		if (subdirs != null)
		{
			for (String subdir : subdirs)
			{
				folder = new File(folder, subdir);
			}
		}

		return new File(folder, filename);
	}

	protected File getTempDir(String fileOrSubFolder)
	{
		List<String> segments = getReference().getSegments(true);
		String path;
		if (CollectionUtils.isEmpty(segments))
			path = "germinate";
		else
			path = segments.get(0);
		File folder = new File(System.getProperty("java.io.tmpdir"), path);
		folder.mkdir();

		return new File(folder, fileOrSubFolder);
	}

	protected File createTempFile(String parentFolder, String filename, String extension, boolean create)
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

		if (create)
			file.createNewFile();

		return file;
	}

	protected File createTempFile(String filename, String extension)
		throws IOException
	{
		return createTempFile(null, filename, extension, true);
	}

	protected synchronized String getFormattedDateTime(Date date)
	{
		return SDF.format(date);
	}

	protected synchronized Date parseDateTime(String date)
		throws ParseException
	{
		return SDF.parse(date);
	}

	protected synchronized String getFormattedDate(Date date)
	{
		return SDF_DATE.format(date);
	}

	protected synchronized Date parseDate(String date)
		throws ParseException
	{
		return SDF_DATE.parse(date);
	}
}
