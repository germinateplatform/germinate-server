package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ClimateStats
{
	private Map<Integer, Integer> dataPointsByYear;
	private Map<Integer, Integer> climateDatasetsPerYear;
	private Map<Integer, Integer> climatesPerYear;
}
