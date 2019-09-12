package jhi.germinate.server.resource;

import org.jooq.*;
import org.restlet.resource.ServerResource;

import java.io.*;
import java.util.*;
import java.util.stream.*;

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

	protected static void exportToFile(PrintWriter bw, Result<Record> results)
	{
		Row row = results.fieldsRow();
		bw.println(row.fieldStream()
					  .map(Field::getName)
					  .collect(Collectors.joining("\t")));
		results.forEach(r -> bw.println(IntStream.range(0, row.size())
												 .boxed()
												 .map(i -> {
													 Object value = r.getValue(i);
													 if (value == null)
														 return "";
													 else
														 return value.toString();
												 })
												 .collect(Collectors.joining("\t"))));
	}
}
