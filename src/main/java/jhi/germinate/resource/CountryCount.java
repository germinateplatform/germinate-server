package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class CountryCount
{
	private Integer id;
	private String  countryName;
	private String  countryCode2;
	private String  countryCode3;
	private Integer count;
}
