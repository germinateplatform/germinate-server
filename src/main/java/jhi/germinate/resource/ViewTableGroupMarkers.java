package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.ViewTableMarkers;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableGroupMarkers extends ViewTableMarkers
{
	private Integer groupId;
}
