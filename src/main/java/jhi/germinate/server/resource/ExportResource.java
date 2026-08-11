package jhi.germinate.server.resource;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.DateTimeUtils;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.io.*;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class ExportResource extends BaseResource
{
	protected static final String CRLF = "\r\n";

	protected File export(TableImpl<? extends Record> table, String name, ExportSettings settings)
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

			return zipFile;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
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
