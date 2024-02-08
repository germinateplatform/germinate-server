package jhi.germinate.resource;

public class DatasetExportRequest extends ExportRequest
{
	private Integer[] datasetIds;

	public Integer[] getDatasetIds()
	{
		return datasetIds;
	}

	public DatasetExportRequest setDatasetIds(Integer[] datasetIds)
	{
		this.datasetIds = datasetIds;
		return this;
	}
}
