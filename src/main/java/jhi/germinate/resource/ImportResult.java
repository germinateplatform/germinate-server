package jhi.germinate.resource;

import jhi.germinate.resource.enums.ImportStatus;

public class ImportResult
{
	private ImportStatus status;
	private int          rowIndex;
	private String       message;

	public ImportResult(ImportStatus status, int rowIndex, String message)
	{
		this.status = status;
		this.rowIndex = rowIndex;
		this.message = message;
	}

	public ImportStatus getStatus()
	{
		return status;
	}

	public int getRowIndex()
	{
		return rowIndex;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString()
	{
		return "ImportResult{" +
			"status=" + status +
			", rowIndex=" + rowIndex +
			", message='" + message + '\'' +
			'}';
	}
}