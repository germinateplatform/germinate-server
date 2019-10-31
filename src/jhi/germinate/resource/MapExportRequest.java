package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class MapExportRequest
{
	private String    format;
	private String    method;
	private String[]  chromosomes;
	private Region[]  regions;
	private Integer[] markerIdInterval;
	private Radius    radius;

	public String getFormat()
	{
		return format;
	}

	public MapExportRequest setFormat(String format)
	{
		this.format = format;
		return this;
	}

	public String getMethod()
	{
		return method;
	}

	public MapExportRequest setMethod(String method)
	{
		this.method = method;
		return this;
	}

	public String[] getChromosomes()
	{
		return chromosomes;
	}

	public MapExportRequest setChromosomes(String[] chromosomes)
	{
		this.chromosomes = chromosomes;
		return this;
	}

	public Region[] getRegions()
	{
		return regions;
	}

	public MapExportRequest setRegions(Region[] regions)
	{
		this.regions = regions;
		return this;
	}

	public Integer[] getMarkerIdInterval()
	{
		return markerIdInterval;
	}

	public MapExportRequest setMarkerIdInterval(Integer[] markerIdInterval)
	{
		this.markerIdInterval = markerIdInterval;
		return this;
	}

	public Radius getRadius()
	{
		return radius;
	}

	public MapExportRequest setRadius(Radius radius)
	{
		this.radius = radius;
		return this;
	}

	public static class Region
	{
		private String chromosome;
		private Double   start;
		private Double   end;

		public String getChromosome()
		{
			return chromosome;
		}

		public Region setChromosome(String chromosome)
		{
			this.chromosome = chromosome;
			return this;
		}

		public Double getStart()
		{
			return start;
		}

		public Region setStart(Double start)
		{
			this.start = start;
			return this;
		}

		public Double getEnd()
		{
			return end;
		}

		public Region setEnd(Double end)
		{
			this.end = end;
			return this;
		}
	}

	public static class Radius
	{
		private Integer markerId;
		private Long    left;
		private Long    right;

		public Integer getMarkerId()
		{
			return markerId;
		}

		public Radius setMarkerId(Integer markerId)
		{
			this.markerId = markerId;
			return this;
		}

		public Long getLeft()
		{
			return left;
		}

		public Radius setLeft(Long left)
		{
			this.left = left;
			return this;
		}

		public Long getRight()
		{
			return right;
		}

		public Radius setRight(Long right)
		{
			this.right = right;
			return this;
		}
	}
}
