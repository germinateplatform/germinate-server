package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewStatsDatasets
{
	private int year;
	private String type;
	private int datasetCount;
	private int dataObjectCount;
	private int dataPointCount;
}
