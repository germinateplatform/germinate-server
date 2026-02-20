package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class VariableUnificationRequest
{
	private Integer   preferredVariableId;
	private Integer[] otherVariableIds;
}
