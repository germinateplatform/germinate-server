package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TraitUnificationRequest
{
	private Integer   preferredTraitId;
	private Integer[] otherTraitIds;
}
