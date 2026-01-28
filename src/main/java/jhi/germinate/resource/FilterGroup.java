package jhi.germinate.resource;

import jhi.germinate.resource.enums.FilterOperator;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class FilterGroup
{
	// These two fields are mutually exclusive. Either you have further groups or no groups and further filters.
	private Filter[]       filters;
	private FilterGroup[]  filterGroups;
	private FilterOperator operator;
}
