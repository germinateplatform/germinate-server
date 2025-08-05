package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableTrialGermplasm extends ViewTableGermplasm
{
	private List<Integer> groupIds;
}
