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
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Sebastian Raubach
 */
public class BaseServerResource extends ServerResource
{
	private static final SimpleDateFormat SDF      = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	protected static final String CRLF = "\r\n";

	/**
	 * Exports the given database result into the writer using the given parameters
	 *
	 * @param bw             The {@link Writer} to export to
	 * @param results        The {@link Result} containing the data
	 * @param includeHeaders Should the column headers of the database result be included?
	 * @param fieldsToIgnore Array containing the fields to ignore from the data. They will not appear in the output.
	 * @throws IOException Thrown if any IO operation fails
	 */
	protected static void exportToFile(Writer bw, Result<? extends Record> results, boolean includeHeaders, Field<?>[] fieldsToIgnore)
		throws IOException
	{
		List<String> columnsToIgnore = fieldsToIgnore == null ? new ArrayList<>() : Arrays.stream(fieldsToIgnore).map(Field::getName).collect(Collectors.toList());
		List<String> columnsToInclude = Arrays.stream(results.fields())
											  .map(Field::getName)
											  .filter(name -> !columnsToIgnore.contains(name))
											  .collect(Collectors.toList());

		Logger.getLogger("").info("FIELDSROW: " + results.fieldsRow().toString());

		if (includeHeaders)
			bw.write(columnsToInclude.stream().collect(Collectors.joining("\t", "", CRLF)));

		Logger.getLogger("").info("COLUMNS: " + columnsToInclude);

		results.forEach(r -> {
			try
			{
				bw.write(columnsToInclude.stream()
										 .map(name -> {
											 Object value = r.getValue(name);
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

	/**
	 * Returns the location of the project's lib filter as a {@link File}
	 *
	 * @return The location of the project's lib filter as a {@link File}
	 * @throws URISyntaxException Thrown if the URI of the folder location is invalid
	 */
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

	/**
	 * Returns the file with the given name from the external data folder in the given sub directory structure
	 *
	 * @param filename The name of the file to return
	 * @param subdirs  Optional sub-directory structure
	 * @return The {@link File} representing the request
	 */
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
