package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.AdditionalExportFormat;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SubsettedGenotypeDatasetRequest extends SubsettedDatasetRequest
{
	private Integer mapId;
	private boolean generateFlapjackProject;
	private boolean generateHapMap;
	private boolean generateFlatFile;

	public AdditionalExportFormat[] getFileTypes() {
		List<AdditionalExportFormat> result = new ArrayList<>();

		if (generateFlapjackProject)
			result.add(AdditionalExportFormat.flapjack);
		if (generateFlatFile)
			result.add(AdditionalExportFormat.text);
		if (generateHapMap)
			result.add(AdditionalExportFormat.hapmap);

		return result.toArray(new AdditionalExportFormat[0]);
	}
}
