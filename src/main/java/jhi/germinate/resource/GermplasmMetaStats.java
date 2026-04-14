package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GermplasmMetaStats
{
	private String  genus;
	private String  species;
	private String  taxonomy;
	private String  sampstat;
	private Integer pdci;
}
