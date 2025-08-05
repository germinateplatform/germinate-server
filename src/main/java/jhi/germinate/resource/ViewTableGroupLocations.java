package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLocations;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableGroupLocations extends ViewTableLocations
{
	private Integer groupId;
}
