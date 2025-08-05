package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TrialCreationDetails
{
	private Integer           datasetId;
	private List<PlotDetails> plots;

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class PlotDetails {
		private Short row;
		private Short column;
		private String germplasm;
		private String rep;
	}
}
