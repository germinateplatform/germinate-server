package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableClimateDataWithGroups extends ViewTableClimateData
{
	private List<Groups> groups;
}
