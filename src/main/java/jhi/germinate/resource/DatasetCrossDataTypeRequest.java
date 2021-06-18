package jhi.germinate.resource;

public class DatasetCrossDataTypeRequest
{
	private Config first;
	private Config second;

	public DatasetCrossDataTypeRequest()
	{
	}

	public Config getFirst()
	{
		return first;
	}

	public DatasetCrossDataTypeRequest setFirst(Config first)
	{
		this.first = first;
		return this;
	}

	public Config getSecond()
	{
		return second;
	}

	public DatasetCrossDataTypeRequest setSecond(Config second)
	{
		this.second = second;
		return this;
	}

	public static class Config
	{
		private Integer   id;
		private String    columnName;
		private DataType  type;
		private Integer[] datasetIds;
		private Integer[] markedIds;
		private Integer[] groupIds;

		public Config()
		{
		}

		public Integer getId()
		{
			return id;
		}

		public Config setId(Integer id)
		{
			this.id = id;
			return this;
		}

		public String getColumnName()
		{
			return columnName;
		}

		public Config setColumnName(String columnName)
		{
			this.columnName = columnName;
			return this;
		}

		public DataType getType()
		{
			return type;
		}

		public Config setType(DataType type)
		{
			this.type = type;
			return this;
		}

		public Integer[] getDatasetIds()
		{
			return datasetIds;
		}

		public Config setDatasetIds(Integer[] datasetIds)
		{
			this.datasetIds = datasetIds;
			return this;
		}

		public Integer[] getMarkedIds()
		{
			return markedIds;
		}

		public Config setMarkedIds(Integer[] markedIds)
		{
			this.markedIds = markedIds;
			return this;
		}

		public Integer[] getGroupIds()
		{
			return groupIds;
		}

		public Config setGroupIds(Integer[] groupIds)
		{
			this.groupIds = groupIds;
			return this;
		}
	}

	public enum DataType
	{
		TRAIT,
		CLIMATE,
		COMPOUND,
		GERMPLASM_COLUMN
	}
}
