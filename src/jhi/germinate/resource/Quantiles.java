package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class Quantiles
{
	private Integer datasetId;
	private Integer traitId;
	private double min;
	private double q1;
	private double median;
	private double q3;
	private double max;
	private double avg;
	private int count;

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public Quantiles setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public Integer getTraitId()
	{
		return traitId;
	}

	public Quantiles setTraitId(Integer traitId)
	{
		this.traitId = traitId;
		return this;
	}

	public double getMin()
	{
		return min;
	}

	public Quantiles setMin(double min)
	{
		this.min = min;
		return this;
	}

	public double getQ1()
	{
		return q1;
	}

	public Quantiles setQ1(double q1)
	{
		this.q1 = q1;
		return this;
	}

	public double getMedian()
	{
		return median;
	}

	public Quantiles setMedian(double median)
	{
		this.median = median;
		return this;
	}

	public double getQ3()
	{
		return q3;
	}

	public Quantiles setQ3(double q3)
	{
		this.q3 = q3;
		return this;
	}

	public double getMax()
	{
		return max;
	}

	public Quantiles setMax(double max)
	{
		this.max = max;
		return this;
	}

	public double getAvg()
	{
		return avg;
	}

	public Quantiles setAvg(double avg)
	{
		this.avg = avg;
		return this;
	}

	public int getCount()
	{
		return count;
	}

	public Quantiles setCount(int count)
	{
		this.count = count;
		return this;
	}
}
