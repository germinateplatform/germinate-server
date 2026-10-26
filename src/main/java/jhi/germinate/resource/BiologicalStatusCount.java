package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class BiologicalStatusCount
{
	private Integer id;
	private String  biologicalstatus;
	private Integer count;
}
