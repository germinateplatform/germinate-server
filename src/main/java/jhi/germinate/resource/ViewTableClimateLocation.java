package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableClimateLocation extends ViewTableLocations
{
	private List<Integer> groupIds;
}
