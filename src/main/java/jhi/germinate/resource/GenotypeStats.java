package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GenotypeStats
{
	private Map<Integer, BigDecimal> dataPointsByYear;
	private Map<Integer, Integer>    genotypeDatasetsPerYear;
	private Map<Integer, Integer>    markersPerYear;
}
