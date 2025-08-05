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
public class DatasetGroupModificationRequest
{
	private Integer   datasetId;
	private Integer[] groupIds;
	private Boolean   addOperation;
}
