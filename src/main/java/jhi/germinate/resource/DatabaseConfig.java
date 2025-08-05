package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DatabaseConfig
{
	private String host;
	private String database;
	private String port;
	private String username;
	private String password;
}
