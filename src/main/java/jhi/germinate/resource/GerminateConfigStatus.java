package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GerminateConfigStatus
{
	private Boolean dbConfigValid;
	private Boolean gkConfigValid;
}
