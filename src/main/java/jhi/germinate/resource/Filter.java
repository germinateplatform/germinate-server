package jhi.germinate.resource;

import jhi.germinate.resource.enums.*;
import jhi.germinate.server.util.StringUtils;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Filter
{
	private String           column;
	private FilterComparator comparator;
	private String[]         values;
	private Boolean          canBeChanged;

	public String getSafeColumn()
	{
		return getSafeColumn(this.column);
	}

	public static String getSafeColumn(String column)
	{
		if (StringUtils.isEmpty(column))
		{
			return "";
		}
		else
		{
			return column.replaceAll("[^a-zA-Z0-9._-]", "").replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
		}
	}
}
