package jhi.germinate.resource;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
public class AsyncExportResult
{
	private String           status;
	private String           uuid;
	private List<ResultFile> files;

	public String getStatus()
	{
		return status;
	}

	public AsyncExportResult setStatus(String status)
	{
		this.status = status;
		return this;
	}

	public String getUuid()
	{
		return uuid;
	}

	public AsyncExportResult setUuid(String uuid)
	{
		this.uuid = uuid;
		return this;
	}

	public List<ResultFile> getFiles()
	{
		return files;
	}

	public AsyncExportResult setFiles(List<ResultFile> files)
	{
		this.files = files;
		return this;
	}
}
