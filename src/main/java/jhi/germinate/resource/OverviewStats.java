package jhi.germinate.resource;

public class OverviewStats
{
	private long germplasm          = 0;
	private long markers            = 0;
	private long maps               = 0;
	private long traits             = 0;
	private long climates           = 0;
	private long locations          = 0;
	private long datasets           = 0;
	private long datasetsGenotype   = 0;
	private long datasetsTrials     = 0;
	private long datasetsAllelefreq = 0;
	private long datasetsClimate    = 0;
	private long datasetsPedigree   = 0;
	private long experiments        = 0;
	private long groups             = 0;
	private long images             = 0;
	private long fileresources      = 0;
	private long publications       = 0;
	private long dataStories        = 0;

	public long getGermplasm()
	{
		return germplasm;
	}

	public void setGermplasm(long germplasm)
	{
		this.germplasm = germplasm;
	}

	public long getMarkers()
	{
		return markers;
	}

	public void setMarkers(long markers)
	{
		this.markers = markers;
	}

	public long getMaps()
	{
		return maps;
	}

	public void setMaps(long maps)
	{
		this.maps = maps;
	}

	public long getTraits()
	{
		return traits;
	}

	public void setTraits(long traits)
	{
		this.traits = traits;
	}

	public long getClimates()
	{
		return climates;
	}

	public void setClimates(long climates)
	{
		this.climates = climates;
	}

	public long getDatasetsPedigree()
	{
		return datasetsPedigree;
	}

	public OverviewStats setDatasetsPedigree(long datasetsPedigree)
	{
		this.datasetsPedigree = datasetsPedigree;
		return this;
	}

	public long getLocations()
	{
		return locations;
	}

	public void setLocations(long locations)
	{
		this.locations = locations;
	}

	public long getDatasets()
	{
		return datasets;
	}

	public void setDatasets(long datasets)
	{
		this.datasets = datasets;
	}

	public long getDatasetsGenotype()
	{
		return datasetsGenotype;
	}

	public void setDatasetsGenotype(long datasetsGenotype)
	{
		this.datasetsGenotype = datasetsGenotype;
	}

	public long getDatasetsTrials()
	{
		return datasetsTrials;
	}

	public void setDatasetsTrials(long datasetsTrials)
	{
		this.datasetsTrials = datasetsTrials;
	}

	public long getDatasetsAllelefreq()
	{
		return datasetsAllelefreq;
	}

	public void setDatasetsAllelefreq(long datasetsAllelefreq)
	{
		this.datasetsAllelefreq = datasetsAllelefreq;
	}

	public long getDatasetsClimate()
	{
		return datasetsClimate;
	}

	public void setDatasetsClimate(long datasetsClimate)
	{
		this.datasetsClimate = datasetsClimate;
	}

	public long getExperiments()
	{
		return experiments;
	}

	public void setExperiments(long experiments)
	{
		this.experiments = experiments;
	}

	public long getGroups()
	{
		return groups;
	}

	public void setGroups(long groups)
	{
		this.groups = groups;
	}

	public long getImages()
	{
		return images;
	}

	public void setImages(long images)
	{
		this.images = images;
	}

	public long getFileresources()
	{
		return fileresources;
	}

	public void setFileresources(long fileresources)
	{
		this.fileresources = fileresources;
	}

	public long getPublications()
	{
		return publications;
	}

	public OverviewStats setPublications(long publications)
	{
		this.publications = publications;
		return this;
	}

	public long getDataStories()
	{
		return dataStories;
	}

	public OverviewStats setDataStories(long dataStories)
	{
		this.dataStories = dataStories;
		return this;
	}
}
