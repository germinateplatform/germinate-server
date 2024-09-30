package jhi.germinate.resource;

public class ProjectStats
{
	private int publicationCount = 0;
	private int groupCount       = 0;
	private int datasetCount     = 0;

	public int getPublicationCount()
	{
		return publicationCount;
	}

	public ProjectStats setPublicationCount(int publicationCount)
	{
		this.publicationCount = publicationCount;
		return this;
	}

	public int getGroupCount()
	{
		return groupCount;
	}

	public ProjectStats setGroupCount(int groupCount)
	{
		this.groupCount = groupCount;
		return this;
	}

	public int getDatasetCount()
	{
		return datasetCount;
	}

	public ProjectStats setDatasetCount(int datasetCount)
	{
		this.datasetCount = datasetCount;
		return this;
	}
}
