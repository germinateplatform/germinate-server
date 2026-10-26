package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TrialSetupStats
{
	private List<String>                           reps;
	private List<Treatments>                       treatments;
	private List<Taxonomies>                       taxonomies;
	private List<TrialCreationDetails.PlotDetails> plots;
	private List<Integer>                          years;
}
