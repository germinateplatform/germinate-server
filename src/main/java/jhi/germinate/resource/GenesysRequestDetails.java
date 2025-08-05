package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GenesysRequestDetails
{
	private String        name;
	private String        email;
	private List<Integer> germplasmIds;
}
