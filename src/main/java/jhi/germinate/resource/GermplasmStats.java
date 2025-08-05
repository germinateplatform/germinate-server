package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GermplasmStats
{
	private Integer    germplasmId;
	private String     germplasmName;
	private Integer    traitId;
	private String     traitName;
	private String     traitNameShort;
	private BigDecimal min;
	private BigDecimal avg;
	private BigDecimal max;
	private Integer    count;
}
