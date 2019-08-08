package jhi.germinate.server.resource;

import org.restlet.resource.*;

import java.net.*;
import java.nio.charset.*;

/**
 * @author Sebastian Raubach
 */
public class PaginatedServerResource extends ServerResource
{
	public static final String PARAM_PREVIOUS_COUNT = "prevCount";
	public static final String PARAM_PAGE           = "page";
	public static final String PARAM_LIMIT          = "limit";
	public static final String PARAM_QUERY          = "query";
	public static final String PARAM_ASCENDING      = "ascending";
	public static final String PARAM_ORDER_BY       = "orderBy";

	protected long    previousCount;
	protected int     currentPage;
	protected int     pageSize;
	protected String  query;
	protected Boolean ascending;
	protected String  orderBy;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.currentPage = Integer.parseInt(getQueryValue(PARAM_PAGE));
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.currentPage = 0;
		}
		try
		{
			this.pageSize = Integer.parseInt(getQueryValue(PARAM_LIMIT));
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.pageSize = Integer.MAX_VALUE;
		}
		try
		{
			this.query = getQueryValue(PARAM_QUERY);
		}
		catch (NullPointerException e)
		{
			this.query = null;
		}
		try
		{
			this.orderBy = getQueryValue(PARAM_ORDER_BY);
		}
		catch (NullPointerException e)
		{
			this.orderBy = null;
		}
		try
		{
			int value = Integer.parseInt(getQueryValue(PARAM_ASCENDING));
			ascending = value == 1;
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.ascending = null;
		}
		try
		{
			this.previousCount = Long.parseLong(getQueryValue(PARAM_PREVIOUS_COUNT));
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.previousCount = -1;
		}
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
