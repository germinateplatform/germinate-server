package jhi.germinate.server.resource.genesys;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GenesysResponse
{
	private String                                            uuid;
	private List<GenesysGermplasmResource.GenesysRequestItem> missingItems;
}
