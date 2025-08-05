package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DatasetCrossDataTypeRequest
{
	private Config first;
	private Config second;

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Config
	{
		private Integer   id;
		private String    columnName;
		private DataType  type;
		private Integer[] datasetIds;
		private Integer[] markedIds;
		private Integer[] groupIds;
	}

	public enum DataType
	{
		TRAIT,
		CLIMATE,
		COMPOUND,
		GERMPLASM_COLUMN
	}
}
