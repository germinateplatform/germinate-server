package jhi.germinate.resource;

import java.util.*;

public class ExportRequest
{
	private Filter[]            filter;
	private Map<String, String> columnNameMapping = new HashMap<>();

	public Filter[] getFilter()
	{
		return filter;
	}

	public ExportRequest setFilter(Filter[] filter)
	{
		this.filter = filter;
		return this;
	}

	public Map<String, String> getColumnNameMapping()
	{
		return columnNameMapping;
	}

	public ExportRequest setColumnNameMapping(Map<String, String> columnNameMapping)
	{
		this.columnNameMapping = columnNameMapping;
		return this;
	}
}
