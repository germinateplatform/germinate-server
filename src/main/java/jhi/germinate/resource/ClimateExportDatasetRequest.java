package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ClimateExportDatasetRequest extends PaginatedRequest
{
	private Integer[] climateIds;
	private Integer[] locationIds;
	private Integer[] locationGroupIds;
	private Integer[] datasetIds;
}
