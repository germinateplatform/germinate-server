package jhi.germinate.resource;

import jhi.germinate.server.util.StringUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;

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
	private String   column;
	private String   comparator;
	private String   operator;
	private String[] values;

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
