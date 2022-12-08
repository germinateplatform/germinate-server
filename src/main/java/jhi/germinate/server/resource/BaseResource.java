package jhi.germinate.server.resource;

import jakarta.ws.rs.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.util.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

public abstract class BaseResource extends ContextResource implements IFilteredResource
{
	@DefaultValue("-1")
	@QueryParam("prevCount")
	protected long previousCount;

	@DefaultValue("0")
	@QueryParam("page")
	protected int currentPage;

	@DefaultValue("2147483647")
	@QueryParam("limit")
	protected int pageSize;

	@QueryParam("ascending")
	private int isAscending;

	protected Boolean ascending = null;

	@QueryParam("orderBy")
	protected String orderBy;

	@QueryParam("minimal")
	@DefaultValue("false")
	protected boolean minimal;

	protected Filter[] filters;

	protected void processRequest(PaginatedRequest request)
	{
		if (request != null)
			this.filters = request.getFilter();

		try
		{
			this.currentPage = request == null ? this.currentPage : request.getPage();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.currentPage = 0;
		}
		try
		{
			this.pageSize = request == null ? this.pageSize : request.getLimit();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.pageSize = Integer.MAX_VALUE;
		}
		try
		{
			this.orderBy = request == null ? this.orderBy : request.getOrderBy();

			if (orderBy != null)
				orderBy = orderBy.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
		}
		catch (NullPointerException e)
		{
			this.orderBy = null;
		}
		try
		{
			Integer value = request == null ? this.isAscending : request.getAscending();
			this.ascending = value == 1;
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.ascending = null;
		}
		try
		{
			this.previousCount = request == null ? this.previousCount : request.getPrevCount();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			this.previousCount = -1L;
		}

		try
		{
			this.minimal = request == null ? this.minimal : request.isMinimal();
		}
		catch (NullPointerException e)
		{
			this.minimal = false;
		}
	}

	protected <T extends Record> SelectForUpdateStep<T> setPaginationAndOrderBy(SelectOrderByStep<T> step)
	{
		if (ascending != null && orderBy != null)
		{
			if (ascending)
				step.orderBy(DSL.field(getSafeColumn(orderBy)).asc());
			else
				step.orderBy(DSL.field(getSafeColumn(orderBy)).desc());
		}

		return step.limit(pageSize)
				   .offset(pageSize * currentPage);
	}

	protected static String getSafeColumn(String column)
	{
		if (StringUtils.isEmpty(column))
		{
			return null;
		}
		else
		{
			return column.replaceAll("[^a-zA-Z0-9._-]", "").replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
		}
	}
}
