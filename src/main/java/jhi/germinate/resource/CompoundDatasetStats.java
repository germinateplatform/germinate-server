package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class CompoundDatasetStats
{
	private Set<ViewTableDatasets>  datasets;
	private Set<ViewTableCompounds> compounds;
	private List<Quantiles>         stats;

	public Set<ViewTableDatasets> getDatasets()
	{
		return datasets;
	}

	public CompoundDatasetStats setDatasets(Set<ViewTableDatasets> datasets)
	{
		this.datasets = datasets;
		return this;
	}

	public Set<ViewTableCompounds> getCompounds()
	{
		return compounds;
	}

	public CompoundDatasetStats setCompounds(Set<ViewTableCompounds> compounds)
	{
		this.compounds = compounds;
		return this;
	}

	public List<Quantiles> getStats()
	{
		return stats;
	}

	public CompoundDatasetStats setStats(List<Quantiles> stats)
	{
		this.stats = stats;
		return this;
	}
}
