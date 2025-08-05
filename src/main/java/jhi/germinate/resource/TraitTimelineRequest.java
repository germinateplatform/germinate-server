package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TraitTimelineRequest
{
	private List<Integer> datasetIds;
	private List<Integer> traitIds;
	private List<Integer> groupIds;
	private List<Integer> markedIds;
}
