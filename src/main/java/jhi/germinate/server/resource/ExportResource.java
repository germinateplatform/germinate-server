package jhi.germinate.server.resource;

import jhi.germinate.server.Database;
import jhi.germinate.server.util.DateTimeUtils;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import jakarta.ws.rs.core.*;
import java.io.*;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class ExportResource extends BaseResource
{
	protected static final String CRLF = "\r\n";

	protected Response export(TableImpl<? extends Record> table, String name, ExportSettings settings)
		throws IOException, SQLException
	{
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		try
		{
			File zipFile = ResourceUtils.createTempFile(null, name + "-" + DateTimeUtils.getFormattedDateTime(new Date()), ".zip", false);

			String prefix = zipFile.getAbsolutePath().replace("\\", "/");
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);

			URI uri = URI.create("jar:file:/" + prefix);

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			env.put("encoding", "UTF-8");

			if (name.endsWith("-"))
				name = name.substring(0, name.length() - 1);

			try (Connection conn = Database.getConnection(true);
				 FileSystem fs = FileSystems.newFileSystem(uri, env, null);
				 PrintWriter bw = new PrintWriter(Files.newBufferedWriter(fs.getPath("/" + name + "-" + DateTimeUtils.getFormattedDateTime(new Date()) + ".txt"), StandardCharsets.UTF_8)))
			{
				DSLContext context = Database.getContext(conn);
				SelectJoinStep<org.jooq.Record> from = context.select()
													 .from(table);

				if (settings != null && settings.conditions != null)
				{
					for (Condition condition : settings.conditions)
						from.where(condition);
				}

				// Filter here!
				where(from, filters);

				ResourceUtils.exportToFileStreamed(bw, setPaginationAndOrderBy(from).fetchLazy(), true, settings != null ? settings.fieldsToNull : null);
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

	protected class ExportSettings
	{
		public Condition[] conditions;
		public Field[]     fieldsToNull;

		public ExportSettings()
		{
		}
	}
}
