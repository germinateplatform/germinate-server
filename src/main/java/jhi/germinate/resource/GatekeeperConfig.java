package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GatekeeperConfig
{
	private String url;
	private String username;
	private String password;
}
