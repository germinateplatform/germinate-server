package jhi.germinate.resource;

import java.util.*;

import jhi.germinate.server.database.codegen.tables.pojos.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateDatasetStats
{
	private Set<ViewTableDatasets> datasets;
	private Set<ViewTableClimates> climates;
	private List<Quantiles>        stats;

	public Set<ViewTableDatasets> getDatasets()
	{
		return datasets;
	}

	public ClimateDatasetStats setDatasets(Set<ViewTableDatasets> datasets)
	{
		this.datasets = datasets;
		return this;
	}

	public Set<ViewTableClimates> getClimates()
	{
		return climates;
	}

	public ClimateDatasetStats setClimates(Set<ViewTableClimates> climates)
	{
		this.climates = climates;
		return this;
	}

	public List<Quantiles> getStats()
	{
		return stats;
	}

	public ClimateDatasetStats setStats(List<Quantiles> stats)
	{
		this.stats = stats;
		return this;
	}
}
