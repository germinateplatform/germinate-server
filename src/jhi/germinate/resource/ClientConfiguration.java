package jhi.germinate.resource;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class ClientConfiguration
{
	private List<String> colorsTemplate;
	private List<String> colorsCharts;

	public ClientConfiguration()
	{
	}

	public List<String> getColorsTemplate()
	{
		return colorsTemplate;
	}

	public ClientConfiguration setColorsTemplate(List<String> colorsTemplate)
	{
		this.colorsTemplate = colorsTemplate;
		return this;
	}

	public List<String> getColorsCharts()
	{
		return colorsCharts;
	}

	public ClientConfiguration setColorsCharts(List<String> colorsCharts)
	{
		this.colorsCharts = colorsCharts;
		return this;
	}
}
