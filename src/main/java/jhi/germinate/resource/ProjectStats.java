package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ProjectStats
{
	private int publicationCount  = 0;
	private int groupCount        = 0;
	private int datasetCount      = 0;
	private int collaboratorCount = 0;
}
