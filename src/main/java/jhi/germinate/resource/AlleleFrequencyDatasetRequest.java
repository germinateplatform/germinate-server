package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.BinningConfig;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class AlleleFrequencyDatasetRequest extends SubsettedGenotypeDatasetRequest
{
	private BinningConfig config;
}
