package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TraitDatasetStats
{
	private Set<ViewTableDatasets> datasets;
	private Set<ViewTableTraits>   traits;
	private Set<Treatments>        treatments;
	private List<Quantiles>        stats;
}
