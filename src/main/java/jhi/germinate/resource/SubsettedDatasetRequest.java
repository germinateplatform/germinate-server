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
public class SubsettedDatasetRequest extends PaginatedRequest
{
	private Integer[] xIds;
	private Integer[] xGroupIds;
	private Integer[] yIds;
	private Integer[] yGroupIds;
	private Integer[] datasetIds;
}
