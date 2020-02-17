package jhi.germinate.server.resource;

import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;

/**
 * @author Sebastian Raubach
 */
public class PaginatedServerResource extends BaseServerResource implements FilteredResource
{
	public static final String PARAM_PREVIOUS_COUNT = "prevCount";
	public static final String PARAM_PAGE           = "page";
	public static final String PARAM_LIMIT          = "limit";
	public static final String PARAM_ASCENDING      = "ascending";
	public static final String PARAM_ORDER_BY       = "orderBy";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

	protected long     previousCount;
	protected int      currentPage;
	protected int      pageSize;
	protected Filter[] filters;
	protected Boolean  ascending;
	protected String   orderBy;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		processRequest(null);
	}

	protected void processRequest(PaginatedRequest request)
	{
		if (request != null)
			this.filters = request.getFilter();

		try
		{
			this.currentPage = request == null ? Integer.parseInt(getQueryValue(PARAM_PAGE)) : request.getPage();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.currentPage = 0;
		}
		try
		{
			this.pageSize = request == null ? Integer.parseInt(getQueryValue(PARAM_LIMIT)) : request.getLimit();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.pageSize = Integer.MAX_VALUE;
		}
		try
		{
			this.orderBy = request == null ? getQueryValue(PARAM_ORDER_BY) : request.getOrderBy();

			if (orderBy != null)
				orderBy = orderBy.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
		}
		catch (NullPointerException e)
		{
			this.orderBy = null;
		}
		try
		{
			Integer value = request == null ? Integer.parseInt(getQueryValue(PARAM_ASCENDING)) : request.getAscending();
			this.ascending = value == 1;
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.ascending = null;
		}
		try
		{
			this.previousCount = request == null ? Long.parseLong(getQueryValue(PARAM_PREVIOUS_COUNT)) : request.getPrevCount();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.previousCount = -1;
		}
	}

	protected <T extends Record> SelectForUpdateStep<T> setPaginationAndOrderBy(SelectOrderByStep<T> step)
	{
		if (ascending != null && orderBy != null)
		{
			if (ascending)
				step.orderBy(DSL.field(orderBy).asc());
			else
				step.orderBy(DSL.field(orderBy).desc());
		}

		return step.limit(pageSize)
				   .offset(pageSize * currentPage);

	}

	protected String getRequestAttributeAsString(String parameter)
	{
		try
		{
			return URLDecoder.decode(getRequestAttributes().get(parameter).toString(), StandardCharsets.UTF_8.name());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public long getPreviousCount()
	{
		return previousCount;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public Filter[] getFilters()
	{
		return filters;
	}

	public Boolean getAscending()
	{
		return ascending;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	protected FileRepresentation export(Result<? extends Record> results, String name)
	{
		FileRepresentation representation;
		try
		{
			File zipFile = createTempFile(null, name + "-" + SDF.format(new Date()), ".zip", false);

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
				 PrintWriter bw = new PrintWriter(Files.newBufferedWriter(fs.getPath("/" + name + "-" + SDF.format(new Date()) + ".txt"), StandardCharsets.UTF_8)))
			{
				exportToFile(bw, results, true, null);
			}

			representation = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
			representation.setSize(zipFile.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
			// Remember to delete this after the call, we don't need it anymore
			representation.setAutoDeleting(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}

	protected FileRepresentation export(TableImpl<? extends Record> table, String name, ExportSettings settings)
	{
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		FileRepresentation representation;
		try
		{
			File zipFile = createTempFile(null, name, ".zip", false);

			String prefix = zipFile.getAbsolutePath().replace("\\", "/");
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);

			URI uri = URI.create("jar:file:/" + prefix);

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			env.put("encoding", "UTF-8");

			if (name.endsWith("-"))
				name = name.substring(0, name.length() - 1);

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 FileSystem fs = FileSystems.newFileSystem(uri, env, null);
				 PrintWriter bw = new PrintWriter(Files.newBufferedWriter(fs.getPath("/" + name + ".txt"), StandardCharsets.UTF_8)))
			{
				SelectJoinStep<Record> from = context.select()
													 .from(table);

				if (settings != null && settings.conditions != null)
				{
					for (Condition condition : settings.conditions)
						from.where(condition);
				}

				// Filter here!
				filter(from, filters);

				exportToFile(bw, setPaginationAndOrderBy(from).fetch(), true, settings);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
			representation.setSize(zipFile.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
			// Remember to delete this after the call, we don't need it anymore
			representation.setAutoDeleting(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
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
