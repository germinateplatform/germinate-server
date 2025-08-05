package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class OverviewStats
{
	private long germplasm          = 0;
	private long markers            = 0;
	private long maps               = 0;
	private long traits             = 0;
	private long climates           = 0;
	private long locations          = 0;
	private long datasets           = 0;
	private long datasetsGenotype   = 0;
	private long datasetsTrials     = 0;
	private long datasetsAllelefreq = 0;
	private long datasetsClimate    = 0;
	private long datasetsPedigree   = 0;
	private long experiments        = 0;
	private long groups             = 0;
	private long images             = 0;
	private long fileresources      = 0;
	private long publications       = 0;
	private long dataStories        = 0;
	private long projects           = 0;
}
