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
public class TrialsExportDatasetRequest extends PaginatedRequest
{
	private Integer[] traitIds;
	private Integer[] germplasmIds;
	private Integer[] germplasmGroupIds;
	private Integer[] datasetIds;
}
