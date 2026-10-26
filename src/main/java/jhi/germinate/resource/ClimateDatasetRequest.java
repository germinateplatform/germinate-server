package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ClimateDatasetRequest extends DatasetRequest
{
	private Integer[] climateIds;
}
