package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class LocaleConfig
{
	private String locale;
	private String name;
	private String flag;
}
