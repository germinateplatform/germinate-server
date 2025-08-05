package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class MapExportRequest
{
	private String    format;
	private String    method;
	private String[]  chromosomes;
	private Region[]  regions;
	private Integer[] markerIdInterval;
	private Radius    radius;

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Region
	{
		private String chromosome;
		private Double start;
		private Double end;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Radius
	{
		private Integer markerId;
		private Long    left;
		private Long    right;
	}
}
