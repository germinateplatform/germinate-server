package jhi.germinate.server.util;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GatekeeperApiError
{
	private int    code;
	private String description;
}
