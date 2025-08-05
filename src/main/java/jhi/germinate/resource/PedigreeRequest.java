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
public class PedigreeRequest extends SubsettedDatasetRequest
{
	private Integer   levelsUp;
	private Integer   levelsDown;
	private Boolean   includeAttributes;
}
