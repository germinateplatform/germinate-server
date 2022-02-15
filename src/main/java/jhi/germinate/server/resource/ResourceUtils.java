package jhi.germinate.server.resource;

import com.google.gson.Gson;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.jooq.*;
import org.jooq.impl.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceUtils
{
	public static final String CRLF = "\r\n";

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

	public static Response exportToZip(Result<? extends Record> results, HttpServletResponse resp, String name)
		throws IOException
	{
		try
		{
			File zipFile = createTempFile(null, name + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".zip", false);

			String prefix = zipFile.getAbsolutePath().replace("\\", "/");
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);

			URI uri = URI.create("jar:file:/" + prefix);

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			env.put("encoding", "UTF-8");

			if (name.endsWith("-"))
				name = name.substring(0, name.length() - 1);

			try (FileSystem fs = FileSystems.newFileSystem(uri, env, null);
				 PrintWriter bw = new PrintWriter(Files.newBufferedWriter(fs.getPath("/" + name + "-" + DateTimeUtils.getFormattedDateTime(new Date()) + ".txt"), StandardCharsets.UTF_8)))
			{
				exportToFile(bw, results, true, null);
			}

			Path zipFilePath = zipFile.toPath();
			return Response.ok((StreamingOutput) output -> {
							   Files.copy(zipFilePath, output);
							   Files.deleteIfExists(zipFilePath);
						   })
						   .type("application/zip")
						   .header("content-disposition", "attachment;filename= \"" + zipFile.getName() + "\"")
						   .header("content-length", zipFile.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		}
	}

	/**
	 * Exports the given database result into the writer using the given parameters
	 *
	 * @param bw             The {@link Writer} to export to
	 * @param results        The {@link Result} containing the data
	 * @param includeHeaders Should the column headers of the database result be included?
	 * @param fieldsToIgnore Array containing the fields to ignore from the data. They will not appear in the output.
	 * @throws IOException Thrown if any IO operation fails
	 */
	public static void exportToFile(Writer bw, Result<? extends Record> results, boolean includeHeaders, Field<?>[] fieldsToIgnore, String... headers)
		throws IOException
	{
		List<String> columnsToIgnore = fieldsToIgnore == null ? new ArrayList<>() : Arrays.stream(fieldsToIgnore).map(Field::getName).collect(Collectors.toList());
		List<String> columnsToInclude = Arrays.stream(results.fields())
											  .map(Field::getName)
											  .filter(name -> !columnsToIgnore.contains(name))
											  .collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(headers))
		{
			for (String header : headers)
			{
				bw.write(header + CRLF);
			}
		}

		if (includeHeaders)
			bw.write(columnsToInclude.stream().collect(Collectors.joining("\t", "", CRLF)));

		Gson gson = new Gson();
		results.forEach(r -> {
			try
			{
				bw.write(columnsToInclude.stream()
										 .map(name -> {
											 Object value = r.get(name);
											 if (value == null)
												 return "";
											 else
												 return value.getClass().isArray() ? gson.toJson(value) : value.toString();
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
	 * Exports the given database result into the writer using the given parameters
	 *
	 * @param bw             The {@link Writer} to export to
	 * @param results        The {@link Result} containing the data
	 * @param includeHeaders Should the column headers of the database result be included?
	 * @param fieldsToIgnore Array containing the fields to ignore from the data. They will not appear in the output.
	 * @throws IOException Thrown if any IO operation fails
	 */
	public static void exportToFileStreamed(Writer bw, Cursor<? extends Record> results, boolean includeHeaders, Field<?>[] fieldsToIgnore)
		throws IOException
	{
		List<String> columnsToIgnore = fieldsToIgnore == null ? new ArrayList<>() : Arrays.stream(fieldsToIgnore).map(Field::getName).collect(Collectors.toList());
		List<String> columnsToInclude = Arrays.stream(results.fields())
											  .map(Field::getName)
											  .filter(name -> !columnsToIgnore.contains(name))
											  .collect(Collectors.toList());

		if (includeHeaders)
			bw.write(columnsToInclude.stream().collect(Collectors.joining("\t", "", CRLF)));

		while (results.hasNext())
		{
			Record r = results.fetchNext();

			if (r == null)
				continue;

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
		}
	}

	public static void resetAutoincrement(DSLContext context, TableImpl<?> table)
	{
		try
		{
			Field<Integer> id = table.field("id", Integer.class);

			Integer maxId = context.select(DSL.max(id)).from(table).fetchAnyInto(Integer.class);

			if (maxId != null)
			{
				context.execute("ALTER TABLE " + table.getName() + " AUTO_INCREMENT = " + maxId);
			}
		}
		catch (Exception e)
		{
			// Do nothing if this fails for ANY reason.
		}
	}

	public static File getTempDir(String fileOrSubFolder)
	{
		// Use the database name here as it's going to be unique per instance and usually path-safe
		String path = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
		File folder = new File(System.getProperty("java.io.tmpdir"), path);
		folder.mkdir();

		return new File(folder, fileOrSubFolder);
	}

	public static File createTempFile(String filename, String extension)
		throws IOException
	{
		return createTempFile(null, filename, extension, true);
	}

	public static File createTempFile(String parentFolder, String filename, String extension, boolean create)
		throws IOException
	{
		extension = extension.replace(".", "");

		// Use the database name here as it's going to be unique per instance and usually path-safe
		String path = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
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

	/**
	 * Returns the file with the given name from the external data folder in the given sub directory structure
	 *
	 * @param filename The name of the file to return
	 * @param subdirs  Optional sub-directory structure
	 * @return The {@link File} representing the request
	 */
	public static File getFromExternal(HttpServletResponse resp, String filename, String... subdirs)
		throws IOException
	{
		File folder = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL));

		if (subdirs != null)
		{
			for (String subdir : subdirs)
			{
				folder = new File(folder, subdir);
			}
		}

		File target = new File(folder, filename);

		if (resp != null && !FileUtils.isSubDirectory(folder, target)) {
			resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
			return null;
		}

		return target;
	}
}
