package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableStories;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableStoriesEnriched extends ViewTableStories
{
	private boolean canAccess = false;
}
