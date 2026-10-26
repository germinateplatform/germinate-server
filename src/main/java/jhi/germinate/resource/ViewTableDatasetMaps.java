package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableMaps;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableDatasetMaps extends ViewTableMaps
{
	private Long mapCoverageCount;
}
