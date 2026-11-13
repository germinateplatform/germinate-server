package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DatasetSimilarityRequest
{
	private List<Integer> datasetIds;
	private String        datasetType;
	private Double        threshold;

	// Getter fallback
	public Double getThreshold()
	{
		return threshold != null ? threshold : 0.5;
	}
}
