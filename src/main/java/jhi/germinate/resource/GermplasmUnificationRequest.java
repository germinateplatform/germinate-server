package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GermplasmUnificationRequest
{
	private Integer   preferredGermplasmId;
	private Integer[] otherGermplasmIds;
	private String    explanation;
}
