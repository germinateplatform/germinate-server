package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UnificationRequest
{
	private Integer   preferredId;
	private Integer[] otherIds;
	private String    explanation;
}
