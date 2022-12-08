package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class TraitDatasetStats
{
	private Set<ViewTableDatasets> datasets;
	private Set<ViewTableTraits>   traits;
	private Set<Treatments>        treatments;
	private List<Quantiles>        stats;

	public Set<ViewTableDatasets> getDatasets()
	{
		return datasets;
	}

	public TraitDatasetStats setDatasets(Set<ViewTableDatasets> datasets)
	{
		this.datasets = datasets;
		return this;
	}

	public Set<ViewTableTraits> getTraits()
	{
		return traits;
	}

	public TraitDatasetStats setTraits(Set<ViewTableTraits> traits)
	{
		this.traits = traits;
		return this;
	}

	public Set<Treatments> getTreatments()
	{
		return treatments;
	}

	public TraitDatasetStats setTreatments(Set<Treatments> treatments)
	{
		this.treatments = treatments;
		return this;
	}

	public List<Quantiles> getStats()
	{
		return stats;
	}

	public TraitDatasetStats setStats(List<Quantiles> stats)
	{
		this.stats = stats;
		return this;
	}
}
