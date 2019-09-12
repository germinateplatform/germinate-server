package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class TrialsExportRequest
{
	private Integer[] xIds;
	private Integer[] xGroupIds;
	private Integer[] yIds;
	private Integer[] yGroupIds;
	private Integer[] datasetIds;

	public Integer[] getxIds()
	{
		return xIds;
	}

	public TrialsExportRequest setxIds(Integer[] xIds)
	{
		this.xIds = xIds;
		return this;
	}

	public Integer[] getxGroupIds()
	{
		return xGroupIds;
	}

	public TrialsExportRequest setxGroupIds(Integer[] xGroupIds)
	{
		this.xGroupIds = xGroupIds;
		return this;
	}

	public Integer[] getyIds()
	{
		return yIds;
	}

	public TrialsExportRequest setyIds(Integer[] yIds)
	{
		this.yIds = yIds;
		return this;
	}

	public Integer[] getyGroupIds()
	{
		return yGroupIds;
	}

	public TrialsExportRequest setyGroupIds(Integer[] yGroupIds)
	{
		this.yGroupIds = yGroupIds;
		return this;
	}

	public Integer[] getDatasetIds()
	{
		return datasetIds;
	}

	public TrialsExportRequest setDatasetIds(Integer[] datasetIds)
	{
		this.datasetIds = datasetIds;
		return this;
	}
}
