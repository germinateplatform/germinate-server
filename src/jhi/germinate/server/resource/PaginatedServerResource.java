package jhi.germinate.server.resource;

import org.jooq.*;
import org.jooq.impl.*;
import org.restlet.resource.*;

import java.net.*;
import java.nio.charset.*;

import jhi.germinate.resource.*;

/**
 * @author Sebastian Raubach
 */
public class PaginatedServerResource extends BaseServerResource
{
	public static final String PARAM_PREVIOUS_COUNT = "prevCount";
	public static final String PARAM_PAGE           = "page";
	public static final String PARAM_LIMIT          = "limit";
	public static final String PARAM_ASCENDING      = "ascending";
	public static final String PARAM_ORDER_BY       = "orderBy";

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

	protected <T extends Record> SelectForUpdateStep<T> setPaginationAndOrderBy(SelectJoinStep<T> step)
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
}
