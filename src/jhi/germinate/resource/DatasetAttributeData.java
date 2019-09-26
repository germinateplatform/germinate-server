package jhi.germinate.resource;

import jhi.germinate.server.database.tables.pojos.ViewTableAttributes;

/**
 * @author Sebastian Raubach
 */
public class DatasetAttributeData extends ViewTableAttributes
{
	private Integer datasetId;
	private String datasetName;
	private String datasetDescription;

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public DatasetAttributeData setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public String getDatasetName()
	{
		return datasetName;
	}

	public DatasetAttributeData setDatasetName(String datasetName)
	{
		this.datasetName = datasetName;
		return this;
	}

	public String getDatasetDescription()
	{
		return datasetDescription;
	}

	public DatasetAttributeData setDatasetDescription(String datasetDescription)
	{
		this.datasetDescription = datasetDescription;
		return this;
	}
}
